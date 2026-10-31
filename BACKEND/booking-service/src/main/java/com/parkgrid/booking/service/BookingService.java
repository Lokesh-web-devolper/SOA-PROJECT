package com.parkgrid.booking.service;

import com.parkgrid.booking.client.BillingClient;
import com.parkgrid.booking.client.ParkingClient;
import com.parkgrid.booking.dto.*;
import com.parkgrid.booking.entity.Booking;
import com.parkgrid.booking.entity.BookingStatus;
import com.parkgrid.booking.exception.BadRequestException;
import com.parkgrid.booking.exception.ResourceNotFoundException;
import com.parkgrid.booking.exception.SlotUnavailableException;
import com.parkgrid.booking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final ParkingClient parkingClient;
    private final BillingClient billingClient;

    public BookingService(BookingRepository bookingRepository, ParkingClient parkingClient, BillingClient billingClient) {
        this.bookingRepository = bookingRepository;
        this.parkingClient = parkingClient;
        this.billingClient = billingClient;
    }

    @Transactional(readOnly = true)
    public SlotAvailabilityDto checkAvailability(Long facilityId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BadRequestException("Start time and end time are required to check slot availability");
        }
        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("Start time must be strictly before end time");
        }

        List<Long> unavailableSlotIds = bookingRepository.findOverlappingSlotIds(facilityId, startTime, endTime);
        if (unavailableSlotIds == null) {
            unavailableSlotIds = Collections.emptyList();
        }
        return new SlotAvailabilityDto(facilityId, startTime, endTime, unavailableSlotIds);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDto createBooking(Long userId, String userEmail, CreateBookingRequest request) {
        if (request.getStartTime().isAfter(request.getExpectedEndTime())) {
            throw new BadRequestException("Start time must be before expected end time");
        }

        if (request.getExpectedEndTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Expected end time cannot be in the past");
        }

        if (request.getVehicleNumber() == null || request.getVehicleNumber().trim().isEmpty()) {
            throw new BadRequestException("Vehicle license plate number is required");
        }

        SlotDto slot;
        try {
            ApiResponse<SlotDto> slotResponse = parkingClient.getSlotById(request.getSlotId());
            if (slotResponse == null || slotResponse.getData() == null) {
                throw new ResourceNotFoundException("Slot not found with id: " + request.getSlotId());
            }
            slot = slotResponse.getData();
        } catch (Exception e) {
            log.error("Failed to query parking service: {}", e.getMessage());
            throw new BadRequestException("Could not verify slot with parking service: " + e.getMessage());
        }

        if (!slot.isActive()) {
            throw new BadRequestException("Slot " + slot.getSlotNumber() + " is inactive and cannot be booked.");
        }

        long overlappingCount = bookingRepository.countOverlappingBookings(
                request.getSlotId(),
                request.getStartTime(),
                request.getExpectedEndTime()
        );

        if (overlappingCount > 0) {
            throw new SlotUnavailableException("This parking slot is no longer available for the selected time. Please select another slot.");
        }

        long minutes = Duration.between(request.getStartTime(), request.getExpectedEndTime()).toMinutes();
        if (minutes < 15) {
            minutes = 15;
        }
        double hours = Math.ceil(minutes / 60.0);
        BigDecimal hourlyRate = slot.getHourlyRate() != null ? slot.getHourlyRate() : BigDecimal.valueOf(15.00);
        BigDecimal estimatedAmount = hourlyRate.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);

        String refDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String bookingReference = "PG-" + refDate + "-" + uniqueSuffix;

        String customerName = request.getCustomerName();
        if (customerName == null || customerName.trim().isEmpty()) {
            customerName = userEmail != null ? userEmail.split("@")[0] : "Customer";
        }

        Booking booking = Booking.builder()
                .bookingReference(bookingReference)
                .userId(userId)
                .userEmail(userEmail)
                .customerName(customerName)
                .vehicleNumber(request.getVehicleNumber().trim().toUpperCase())
                .facilityId(slot.getFacilityId())
                .facilityName(slot.getFacilityName())
                .zoneId(slot.getZoneId())
                .zoneName(slot.getZoneName())
                .slotId(slot.getId())
                .slotNumber(slot.getSlotNumber())
                .startTime(request.getStartTime())
                .expectedEndTime(request.getExpectedEndTime())
                .bookingStatus(BookingStatus.CONFIRMED)
                .hourlyRate(hourlyRate)
                .estimatedAmount(estimatedAmount)
                .build();

        try {
            Booking saved = bookingRepository.save(booking);
            log.info("Booking created successfully: ref={}, slot={}, user={}, vehicle={}",
                    bookingReference, slot.getSlotNumber(), userId, request.getVehicleNumber());
            return mapToDto(saved);
        } catch (DataIntegrityViolationException ex) {
            log.error("Concurrent double-booking constraint violation for slot {}: {}", slot.getId(), ex.getMessage());
            throw new SlotUnavailableException("This parking slot is no longer available for the selected time. Please select another slot.");
        }
    }

    @Transactional
    public BookingDto checkIn(Long bookingId, Long userId, String userRole) {
        Booking booking = getBookingAndVerifyOwnership(bookingId, userId, userRole);

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED && booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Cannot check in. Current booking status is: " + booking.getBookingStatus());
        }

        booking.setActualEntryTime(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.ACTIVE);
        Booking updated = bookingRepository.save(booking);

        try {
            parkingClient.updateSlotStatus(booking.getSlotId(), new SlotStatusUpdateRequest("OCCUPIED"));
        } catch (Exception ex) {
            log.warn("Failed to notify parking service of check-in occupancy: {}", ex.getMessage());
        }

        return mapToDto(updated);
    }

    @Transactional
    public BookingDto checkOut(Long bookingId, Long userId, String userRole) {
        Booking booking = getBookingAndVerifyOwnership(bookingId, userId, userRole);

        if (booking.getBookingStatus() != BookingStatus.ACTIVE) {
            throw new BadRequestException("Cannot check out. Booking is not in ACTIVE state. Current status: " + booking.getBookingStatus());
        }

        LocalDateTime exitTime = LocalDateTime.now();
        booking.setActualExitTime(exitTime);
        booking.setBookingStatus(BookingStatus.COMPLETED);

        BigDecimal finalCharge = booking.getEstimatedAmount();
        try {
            CreateInvoiceRequest invoiceRequest = CreateInvoiceRequest.builder()
                    .bookingId(booking.getId())
                    .bookingReference(booking.getBookingReference())
                    .userId(booking.getUserId())
                    .userEmail(booking.getUserEmail())
                    .facilityId(booking.getFacilityId())
                    .entryTime(booking.getActualEntryTime() != null ? booking.getActualEntryTime() : booking.getStartTime())
                    .exitTime(exitTime)
                    .hourlyRate(booking.getHourlyRate())
                    .build();

            ApiResponse<InvoiceDto> invoiceResponse = billingClient.createInvoice(invoiceRequest);
            if (invoiceResponse != null && invoiceResponse.getData() != null) {
                finalCharge = invoiceResponse.getData().getTotalAmount();
            }
        } catch (Exception ex) {
            log.error("Could not generate invoice with billing service: {}", ex.getMessage());
        }

        booking.setFinalAmount(finalCharge);
        Booking updated = bookingRepository.save(booking);

        try {
            parkingClient.updateSlotStatus(booking.getSlotId(), new SlotStatusUpdateRequest("AVAILABLE"));
        } catch (Exception ex) {
            log.warn("Failed to release parking slot after checkout: {}", ex.getMessage());
        }

        return mapToDto(updated);
    }

    @Transactional
    public BookingDto cancelBooking(Long bookingId, Long userId, String userRole) {
        Booking booking = getBookingAndVerifyOwnership(bookingId, userId, userRole);

        if (booking.getBookingStatus() == BookingStatus.ACTIVE || booking.getBookingStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Active or completed bookings cannot be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);

        try {
            parkingClient.updateSlotStatus(booking.getSlotId(), new SlotStatusUpdateRequest("AVAILABLE"));
        } catch (Exception ignored) {}

        return mapToDto(updated);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getMyBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long id, Long userId, String userRole) {
        Booking booking = getBookingAndVerifyOwnership(id, userId, userRole);
        return mapToDto(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookings(Long facilityId, BookingStatus status) {
        return bookingRepository.searchBookings(facilityId, status)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private Booking getBookingAndVerifyOwnership(Long bookingId, Long userId, String userRole) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!"ROLE_ADMIN".equals(userRole) && !booking.getUserId().equals(userId)) {
            throw new BadRequestException("You are not authorized to view or modify this booking");
        }
        return booking;
    }

    private BookingDto mapToDto(Booking b) {
        return BookingDto.builder()
                .id(b.getId())
                .bookingReference(b.getBookingReference())
                .userId(b.getUserId())
                .userEmail(b.getUserEmail())
                .customerName(b.getCustomerName())
                .vehicleNumber(b.getVehicleNumber())
                .facilityId(b.getFacilityId())
                .facilityName(b.getFacilityName())
                .zoneId(b.getZoneId())
                .zoneName(b.getZoneName())
                .slotId(b.getSlotId())
                .slotNumber(b.getSlotNumber())
                .startTime(b.getStartTime())
                .expectedEndTime(b.getExpectedEndTime())
                .actualEntryTime(b.getActualEntryTime())
                .actualExitTime(b.getActualExitTime())
                .bookingStatus(b.getBookingStatus())
                .hourlyRate(b.getHourlyRate())
                .estimatedAmount(b.getEstimatedAmount())
                .finalAmount(b.getFinalAmount())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}

package com.parkgrid.booking.service;

import com.parkgrid.booking.client.BillingClient;
import com.parkgrid.booking.client.ParkingClient;
import com.parkgrid.booking.dto.*;
import com.parkgrid.booking.entity.Booking;
import com.parkgrid.booking.entity.BookingStatus;
import com.parkgrid.booking.exception.BadRequestException;
import com.parkgrid.booking.exception.SlotUnavailableException;
import com.parkgrid.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ParkingClient parkingClient;

    @Mock
    private BillingClient billingClient;

    @InjectMocks
    private BookingService bookingService;

    private SlotDto sampleSlot;

    @BeforeEach
    void setUp() {
        sampleSlot = new SlotDto();
        sampleSlot.setId(101L);
        sampleSlot.setSlotNumber("A-101");
        sampleSlot.setFacilityId(1L);
        sampleSlot.setFacilityName("Downtown Central Plaza");
        sampleSlot.setZoneId(1L);
        sampleSlot.setZoneName("Zone A");
        sampleSlot.setSlotType("STANDARD");
        sampleSlot.setStatus("AVAILABLE");
        sampleSlot.setActive(true);
        sampleSlot.setHourlyRate(new BigDecimal("20.00"));
    }

    @Test
    @DisplayName("Test 1: Customer 1 books A101 successfully when slot is available")
    void test1_createBooking_Success() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(3);

        CreateBookingRequest request = new CreateBookingRequest(
                1L, 1L, 101L, start, end, "CA-123-XY", "Alice Driver"
        );

        when(parkingClient.getSlotById(101L)).thenReturn(ApiResponse.success("Success", sampleSlot));
        when(bookingRepository.countOverlappingBookings(101L, start, end)).thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });

        BookingDto result = bookingService.createBooking(100L, "alice@example.com", request);

        assertNotNull(result);
        assertEquals("A-101", result.getSlotNumber());
        assertEquals("CA-123-XY", result.getVehicleNumber());
        assertEquals(BookingStatus.CONFIRMED, result.getBookingStatus());
        assertEquals(new BigDecimal("40.00"), result.getEstimatedAmount());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Test 2 & 3: Customer 2 attempts to book A101 for overlapping period -> SlotUnavailableException")
    void test2_and_3_createBooking_Overlapping_ThrowsConflict() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(3);

        CreateBookingRequest request = new CreateBookingRequest(
                1L, 1L, 101L, start, end, "NY-999-ZZ", "Bob Driver"
        );

        when(parkingClient.getSlotById(101L)).thenReturn(ApiResponse.success("Success", sampleSlot));
        // Overlapping count > 0 indicates another booking already occupies the time range
        when(bookingRepository.countOverlappingBookings(101L, start, end)).thenReturn(1L);

        SlotUnavailableException ex = assertThrows(SlotUnavailableException.class, () ->
                bookingService.createBooking(101L, "bob@example.com", request)
        );

        assertTrue(ex.getMessage().contains("no longer available"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Test 4: Customer checks time-interval availability and detects reserved slot")
    void test4_checkAvailability_DetectsOverlaps() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(3);

        when(bookingRepository.findOverlappingSlotIds(1L, start, end)).thenReturn(List.of(101L));

        SlotAvailabilityDto availability = bookingService.checkAvailability(1L, start, end);

        assertNotNull(availability);
        assertEquals(1, availability.getTotalUnavailable());
        assertTrue(availability.getUnavailableSlotIds().contains(101L));
    }

    @Test
    @DisplayName("Test 7: Customer checks out -> triggers Billing Service invoice & completes booking")
    void test7_checkOut_TriggersBillingInvoice() {
        Booking activeBooking = Booking.builder()
                .id(1L)
                .bookingReference("PG-20261030-TEST1")
                .userId(100L)
                .userEmail("alice@example.com")
                .facilityId(1L)
                .slotId(101L)
                .slotNumber("A-101")
                .bookingStatus(BookingStatus.ACTIVE)
                .startTime(LocalDateTime.now().minusHours(2))
                .expectedEndTime(LocalDateTime.now().plusHours(1))
                .actualEntryTime(LocalDateTime.now().minusHours(2))
                .hourlyRate(new BigDecimal("20.00"))
                .estimatedAmount(new BigDecimal("60.00"))
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(activeBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceDto invoice = new InvoiceDto();
        invoice.setId(500L);
        invoice.setTotalAmount(new BigDecimal("40.00"));
        when(billingClient.createInvoice(any(CreateInvoiceRequest.class))).thenReturn(ApiResponse.success("Invoice created", invoice));

        BookingDto result = bookingService.checkOut(1L, 100L, "ROLE_CUSTOMER");

        assertNotNull(result);
        assertEquals(BookingStatus.COMPLETED, result.getBookingStatus());
        assertEquals(new BigDecimal("40.00"), result.getFinalAmount());
        assertNotNull(result.getActualExitTime());
        verify(billingClient, times(1)).createInvoice(any(CreateInvoiceRequest.class));
    }

    @Test
    @DisplayName("Test 9: Customer cancels booking -> status changes to CANCELLED and slot is released")
    void test9_cancelBooking_ReleasesSlot() {
        Booking confirmedBooking = Booking.builder()
                .id(2L)
                .bookingReference("PG-20261030-TEST2")
                .userId(100L)
                .userEmail("alice@example.com")
                .facilityId(1L)
                .slotId(101L)
                .slotNumber("A-101")
                .bookingStatus(BookingStatus.CONFIRMED)
                .startTime(LocalDateTime.now().plusHours(2))
                .expectedEndTime(LocalDateTime.now().plusHours(4))
                .build();

        when(bookingRepository.findById(2L)).thenReturn(Optional.of(confirmedBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.cancelBooking(2L, 100L, "ROLE_CUSTOMER");

        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, result.getBookingStatus());
        verify(parkingClient, times(1)).updateSlotStatus(eq(101L), any());
    }
}

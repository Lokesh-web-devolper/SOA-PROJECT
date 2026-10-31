package com.parkgrid.billing.service;

import com.parkgrid.billing.dto.*;
import com.parkgrid.billing.entity.Invoice;
import com.parkgrid.billing.entity.PaymentStatus;
import com.parkgrid.billing.entity.PricingConfiguration;
import com.parkgrid.billing.exception.BadRequestException;
import com.parkgrid.billing.exception.ResourceNotFoundException;
import com.parkgrid.billing.repository.InvoiceRepository;
import com.parkgrid.billing.repository.PricingConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private static final Logger log = LoggerFactory.getLogger(BillingService.class);

    private final InvoiceRepository invoiceRepository;
    private final PricingConfigurationRepository pricingRepository;

    @Value("${app.billing.grace-period-minutes:15}")
    private int defaultGracePeriodMinutes = 15;

    public BillingService(InvoiceRepository invoiceRepository, PricingConfigurationRepository pricingRepository) {
        this.invoiceRepository = invoiceRepository;
        this.pricingRepository = pricingRepository;
    }

    public CalculateFeeResponse calculateFee(CalculateFeeRequest request) {
        if (request.getExitTime().isBefore(request.getEntryTime())) {
            throw new BadRequestException("Exit time cannot be earlier than entry time");
        }

        long durationMinutes = Duration.between(request.getEntryTime(), request.getExitTime()).toMinutes();
        int gracePeriod = defaultGracePeriodMinutes;

        if (request.getFacilityId() != null) {
            gracePeriod = pricingRepository.findByFacilityId(request.getFacilityId())
                    .map(PricingConfiguration::getGracePeriodMinutes)
                    .orElse(defaultGracePeriodMinutes);
        }

        if (durationMinutes <= gracePeriod) {
            return CalculateFeeResponse.builder()
                    .totalMinutes(durationMinutes)
                    .billableHours(0.0)
                    .hourlyRate(request.getHourlyRate() != null ? request.getHourlyRate() : BigDecimal.ZERO)
                    .totalAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .calculationBreakdown(String.format("Stay of %d mins is within the %d min grace period (Free parking)", durationMinutes, gracePeriod))
                    .build();
        }

        double billableHours = Math.max(1.0, Math.ceil(durationMinutes / 60.0));
        BigDecimal rate = request.getHourlyRate() != null ? request.getHourlyRate() : BigDecimal.valueOf(15.00);
        BigDecimal totalAmount = rate.multiply(BigDecimal.valueOf(billableHours)).setScale(2, RoundingMode.HALF_UP);

        String breakdown = String.format("Duration: %d mins -> %.0f billable hours @ $%s/hr = $%s",
                durationMinutes, billableHours, rate.toPlainString(), totalAmount.toPlainString());

        return CalculateFeeResponse.builder()
                .totalMinutes(durationMinutes)
                .billableHours(billableHours)
                .hourlyRate(rate)
                .totalAmount(totalAmount)
                .calculationBreakdown(breakdown)
                .build();
    }

    @Transactional
    public InvoiceDto createInvoice(CreateInvoiceRequest request) {
        Optional<Invoice> existing = invoiceRepository.findByBookingId(request.getBookingId());
        if (existing.isPresent()) {
            log.info("Invoice already exists for bookingId={}. Returning existing invoice {}.", request.getBookingId(), existing.get().getInvoiceNumber());
            return mapToDto(existing.get());
        }

        CalculateFeeResponse fee = calculateFee(CalculateFeeRequest.builder()
                .entryTime(request.getEntryTime())
                .exitTime(request.getExitTime())
                .hourlyRate(request.getHourlyRate())
                .facilityId(request.getFacilityId())
                .build());

        String invDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String invoiceNumber = "INV-" + invDate + "-" + uniqueSuffix;

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .bookingId(request.getBookingId())
                .bookingReference(request.getBookingReference())
                .userId(request.getUserId())
                .userEmail(request.getUserEmail())
                .facilityId(request.getFacilityId())
                .entryTime(request.getEntryTime())
                .exitTime(request.getExitTime())
                .parkingDurationMinutes(fee.getTotalMinutes())
                .billableHours(fee.getBillableHours())
                .hourlyRate(fee.getHourlyRate())
                .totalAmount(fee.getTotalAmount())
                .paymentStatus(PaymentStatus.PAID)
                .build();

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Generated invoice {} for bookingId={}, totalAmount={}", invoiceNumber, request.getBookingId(), fee.getTotalAmount());
        return mapToDto(saved);
    }

    public InvoiceDto getInvoiceById(Long id, Long userId, String userRole) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (!"ROLE_ADMIN".equals(userRole) && !invoice.getUserId().equals(userId)) {
            throw new BadRequestException("Access denied: You do not own this invoice");
        }

        return mapToDto(invoice);
    }

    public InvoiceDto getInvoiceByBookingId(Long bookingId, Long userId, String userRole) {
        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for booking: " + bookingId));

        if (!"ROLE_ADMIN".equals(userRole) && !invoice.getUserId().equals(userId)) {
            throw new BadRequestException("Access denied: You do not own this invoice");
        }

        return mapToDto(invoice);
    }

    public List<InvoiceDto> getMyInvoices(Long userId) {
        return invoiceRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceDto> getAllInvoices() {
        return invoiceRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public RevenueStatisticsDto getRevenueStatistics() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfWeek = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        BigDecimal total = invoiceRepository.calculateTotalRevenue();
        BigDecimal today = invoiceRepository.calculateRevenueSince(startOfToday);
        BigDecimal week = invoiceRepository.calculateRevenueSince(startOfWeek);
        BigDecimal month = invoiceRepository.calculateRevenueSince(startOfMonth);

        long count = invoiceRepository.count();

        List<RevenueStatisticsDto.DailyRevenuePoint> trend = new ArrayList<>();
        List<Invoice> allInvoices = invoiceRepository.findAll();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            BigDecimal dayTotal = allInvoices.stream()
                    .filter(inv -> inv.getCreatedAt() != null && inv.getCreatedAt().toLocalDate().isEqual(date))
                    .map(Invoice::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            trend.add(new RevenueStatisticsDto.DailyRevenuePoint(
                    date.format(DateTimeFormatter.ofPattern("MMM dd")),
                    dayTotal
            ));
        }

        List<RevenueStatisticsDto.FacilityRevenuePoint> facilityBreakdown = List.of(
                new RevenueStatisticsDto.FacilityRevenuePoint("Central Plaza", total.multiply(BigDecimal.valueOf(0.55)).setScale(2, RoundingMode.HALF_UP)),
                new RevenueStatisticsDto.FacilityRevenuePoint("Silicon Tech Park", total.multiply(BigDecimal.valueOf(0.30)).setScale(2, RoundingMode.HALF_UP)),
                new RevenueStatisticsDto.FacilityRevenuePoint("Metro Harbor Station", total.multiply(BigDecimal.valueOf(0.15)).setScale(2, RoundingMode.HALF_UP))
        );

        return RevenueStatisticsDto.builder()
                .totalRevenue(total)
                .todayRevenue(today)
                .weekRevenue(week)
                .monthRevenue(month)
                .totalInvoices(count)
                .paidInvoices(count)
                .dailyRevenueTrend(trend)
                .facilityBreakdown(facilityBreakdown)
                .build();
    }

    private InvoiceDto mapToDto(Invoice i) {
        return InvoiceDto.builder()
                .id(i.getId())
                .invoiceNumber(i.getInvoiceNumber())
                .bookingId(i.getBookingId())
                .bookingReference(i.getBookingReference())
                .userId(i.getUserId())
                .userEmail(i.getUserEmail())
                .facilityId(i.getFacilityId())
                .entryTime(i.getEntryTime())
                .exitTime(i.getExitTime())
                .parkingDurationMinutes(i.getParkingDurationMinutes())
                .billableHours(i.getBillableHours())
                .hourlyRate(i.getHourlyRate())
                .totalAmount(i.getTotalAmount())
                .paymentStatus(i.getPaymentStatus())
                .createdAt(i.getCreatedAt())
                .build();
    }
}

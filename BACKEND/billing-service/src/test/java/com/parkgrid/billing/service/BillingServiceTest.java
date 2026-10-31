package com.parkgrid.billing.service;

import com.parkgrid.billing.dto.CalculateFeeRequest;
import com.parkgrid.billing.dto.CalculateFeeResponse;
import com.parkgrid.billing.dto.CreateInvoiceRequest;
import com.parkgrid.billing.dto.InvoiceDto;
import com.parkgrid.billing.entity.Invoice;
import com.parkgrid.billing.entity.PaymentStatus;
import com.parkgrid.billing.repository.InvoiceRepository;
import com.parkgrid.billing.repository.PricingConfigurationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PricingConfigurationRepository pricingRepository;

    @InjectMocks
    private BillingService billingService;

    @Test
    @DisplayName("Billing Test 1: Visits within 15-minute grace period incur zero charges")
    void test1_calculateFee_GracePeriod_Free() {
        LocalDateTime entry = LocalDateTime.now().minusMinutes(12);
        LocalDateTime exit = LocalDateTime.now();

        CalculateFeeRequest req = CalculateFeeRequest.builder()
                .entryTime(entry)
                .exitTime(exit)
                .hourlyRate(new BigDecimal("30.00"))
                .build();

        CalculateFeeResponse res = billingService.calculateFee(req);

        assertNotNull(res);
        assertEquals(0.0, res.getBillableHours());
        assertEquals(new BigDecimal("0.00"), res.getTotalAmount());
        assertTrue(res.getCalculationBreakdown().contains("grace period"));
    }

    @Test
    @DisplayName("Billing Test 2: Standard duration (2 hours) at rate ₹30 = ₹60")
    void test2_calculateFee_Exact2Hours() {
        LocalDateTime entry = LocalDateTime.now().minusHours(2);
        LocalDateTime exit = LocalDateTime.now();

        CalculateFeeRequest req = CalculateFeeRequest.builder()
                .entryTime(entry)
                .exitTime(exit)
                .hourlyRate(new BigDecimal("30.00"))
                .build();

        CalculateFeeResponse res = billingService.calculateFee(req);

        assertNotNull(res);
        assertEquals(2.0, res.getBillableHours());
        assertEquals(new BigDecimal("60.00"), res.getTotalAmount());
    }

    @Test
    @DisplayName("Billing Test 3: Partial hour ceiling (2 hours 30 mins) rounded up to 3 hours = ₹90")
    void test3_calculateFee_PartialHourCeiling() {
        LocalDateTime entry = LocalDateTime.now().minusMinutes(150); // 2.5 hours
        LocalDateTime exit = LocalDateTime.now();

        CalculateFeeRequest req = CalculateFeeRequest.builder()
                .entryTime(entry)
                .exitTime(exit)
                .hourlyRate(new BigDecimal("30.00"))
                .build();

        CalculateFeeResponse res = billingService.calculateFee(req);

        assertNotNull(res);
        assertEquals(3.0, res.getBillableHours());
        assertEquals(new BigDecimal("90.00"), res.getTotalAmount());
    }

    @Test
    @DisplayName("Billing Test 4: Create invoice generates unique reference and returns invoice")
    void test4_createInvoice_Success() {
        LocalDateTime entry = LocalDateTime.now().minusHours(2);
        LocalDateTime exit = LocalDateTime.now();

        CreateInvoiceRequest req = CreateInvoiceRequest.builder()
                .bookingId(101L)
                .bookingReference("PG-20261030-001")
                .userId(1L)
                .userEmail("customer@parkgrid.com")
                .entryTime(entry)
                .exitTime(exit)
                .hourlyRate(new BigDecimal("25.00"))
                .build();

        when(invoiceRepository.findByBookingId(101L)).thenReturn(Optional.empty());
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> {
            Invoice inv = i.getArgument(0);
            inv.setId(1L);
            return inv;
        });

        InvoiceDto invoice = billingService.createInvoice(req);

        assertNotNull(invoice);
        assertEquals(101L, invoice.getBookingId());
        assertEquals(new BigDecimal("50.00"), invoice.getTotalAmount());
        assertEquals(PaymentStatus.PAID, invoice.getPaymentStatus());
        assertTrue(invoice.getInvoiceNumber().startsWith("INV-"));
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Billing Test 5: Idempotency check prevents duplicate invoices on checkout retries")
    void test5_createInvoice_Idempotent() {
        LocalDateTime entry = LocalDateTime.now().minusHours(1);
        LocalDateTime exit = LocalDateTime.now();

        Invoice existing = Invoice.builder()
                .id(99L)
                .invoiceNumber("INV-20261030-ALREADY")
                .bookingId(102L)
                .userId(1L)
                .totalAmount(new BigDecimal("30.00"))
                .paymentStatus(PaymentStatus.PAID)
                .build();

        CreateInvoiceRequest req = CreateInvoiceRequest.builder()
                .bookingId(102L)
                .entryTime(entry)
                .exitTime(exit)
                .build();

        when(invoiceRepository.findByBookingId(102L)).thenReturn(Optional.of(existing));

        InvoiceDto invoice = billingService.createInvoice(req);

        assertNotNull(invoice);
        assertEquals("INV-20261030-ALREADY", invoice.getInvoiceNumber());
        assertEquals(new BigDecimal("30.00"), invoice.getTotalAmount());
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }
}

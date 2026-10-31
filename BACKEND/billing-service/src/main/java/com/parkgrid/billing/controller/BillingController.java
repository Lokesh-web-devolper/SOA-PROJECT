package com.parkgrid.billing.controller;

import com.parkgrid.billing.dto.*;
import com.parkgrid.billing.security.JwtUtil;
import com.parkgrid.billing.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@Tag(name = "Billing & Invoicing", description = "Endpoints for parking fee calculation, invoice retrieval, and revenue analytics")
public class BillingController {

    private final BillingService billingService;
    private final JwtUtil jwtUtil;

    public BillingController(BillingService billingService, JwtUtil jwtUtil) {
        this.billingService = billingService;
        this.jwtUtil = jwtUtil;
    }

    private Long extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return jwtUtil.getUserIdFromToken(header.substring(7));
        }
        return null;
    }

    private String extractUserRole(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return jwtUtil.getRoleFromToken(header.substring(7));
        }
        return "ROLE_CUSTOMER";
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate dynamic parking fee based on entry and exit timestamps")
    public ResponseEntity<ApiResponse<CalculateFeeResponse>> calculateFee(@Valid @RequestBody CalculateFeeRequest request) {
        CalculateFeeResponse response = billingService.calculateFee(request);
        return ResponseEntity.ok(ApiResponse.success("Fee calculated successfully", response));
    }

    @PostMapping("/invoices")
    @Operation(summary = "Generate an invoice upon booking completion (idempotent)")
    public ResponseEntity<ApiResponse<InvoiceDto>> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceDto invoice = billingService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invoice generated successfully", invoice));
    }

    @GetMapping("/invoices/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ApiResponse<InvoiceDto>> getInvoiceById(@PathVariable Long id, HttpServletRequest servletRequest) {
        Long userId = extractUserId(servletRequest);
        String role = extractUserRole(servletRequest);
        InvoiceDto invoice = billingService.getInvoiceById(id, userId, role);
        return ResponseEntity.ok(ApiResponse.success("Invoice fetched successfully", invoice));
    }

    @GetMapping("/invoices/booking/{bookingId}")
    @Operation(summary = "Get invoice by booking ID")
    public ResponseEntity<ApiResponse<InvoiceDto>> getInvoiceByBookingId(@PathVariable Long bookingId, HttpServletRequest servletRequest) {
        Long userId = extractUserId(servletRequest);
        String role = extractUserRole(servletRequest);
        InvoiceDto invoice = billingService.getInvoiceByBookingId(bookingId, userId, role);
        return ResponseEntity.ok(ApiResponse.success("Invoice fetched successfully", invoice));
    }

    @GetMapping("/my-invoices")
    @Operation(summary = "Get billing history for authenticated customer")
    public ResponseEntity<ApiResponse<List<InvoiceDto>>> getMyInvoices(HttpServletRequest servletRequest) {
        Long userId = extractUserId(servletRequest);
        List<InvoiceDto> invoices = billingService.getMyInvoices(userId);
        return ResponseEntity.ok(ApiResponse.success("Invoices fetched successfully", invoices));
    }

    @GetMapping("/invoices")
    @Operation(summary = "Admin: View all generated invoices")
    public ResponseEntity<ApiResponse<List<InvoiceDto>>> getAllInvoices() {
        List<InvoiceDto> invoices = billingService.getAllInvoices();
        return ResponseEntity.ok(ApiResponse.success("All invoices fetched successfully", invoices));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Admin: Get revenue metrics and daily trends")
    public ResponseEntity<ApiResponse<RevenueStatisticsDto>> getRevenueStatistics() {
        RevenueStatisticsDto stats = billingService.getRevenueStatistics();
        return ResponseEntity.ok(ApiResponse.success("Revenue statistics fetched successfully", stats));
    }
}

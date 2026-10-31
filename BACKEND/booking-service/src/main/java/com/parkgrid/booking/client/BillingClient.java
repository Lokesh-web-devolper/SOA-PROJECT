package com.parkgrid.booking.client;

import com.parkgrid.booking.dto.ApiResponse;
import com.parkgrid.booking.dto.CalculateFeeRequest;
import com.parkgrid.booking.dto.CalculateFeeResponse;
import com.parkgrid.booking.dto.CreateInvoiceRequest;
import com.parkgrid.booking.dto.InvoiceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "billing-service")
public interface BillingClient {

    @PostMapping("/api/billing/calculate")
    ApiResponse<CalculateFeeResponse> calculateFee(@RequestBody CalculateFeeRequest request);

    @PostMapping("/api/billing/invoices")
    ApiResponse<InvoiceDto> createInvoice(@RequestBody CreateInvoiceRequest request);
}

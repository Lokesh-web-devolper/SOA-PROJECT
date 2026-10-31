package com.parkgrid.booking.client;

import com.parkgrid.booking.dto.ApiResponse;
import com.parkgrid.booking.dto.SlotDto;
import com.parkgrid.booking.dto.SlotStatusUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "parking-service")
public interface ParkingClient {

    @GetMapping("/api/parking/slots/{id}")
    ApiResponse<SlotDto> getSlotById(@PathVariable("id") Long id);

    @PutMapping("/api/parking/slots/{id}/status")
    ApiResponse<SlotDto> updateSlotStatus(@PathVariable("id") Long id, @RequestBody SlotStatusUpdateRequest request);
}

package com.parkgrid.parking.controller;

import com.parkgrid.parking.dto.ApiResponse;
import com.parkgrid.parking.dto.SlotDto;
import com.parkgrid.parking.dto.SlotStatusUpdateRequest;
import com.parkgrid.parking.entity.SlotStatus;
import com.parkgrid.parking.entity.SlotType;
import com.parkgrid.parking.service.ParkingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking/slots")
@Tag(name = "Parking Slots", description = "Endpoints for slot search, real-time status management, and slot administration")
public class SlotController {

    private final ParkingSlotService slotService;

    public SlotController(ParkingSlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping
    @Operation(summary = "Search parking slots by facility, zone, status, or type")
    public ResponseEntity<ApiResponse<List<SlotDto>>> searchSlots(
            @RequestParam(required = false) Long facilityId,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) SlotStatus status,
            @RequestParam(required = false) SlotType slotType) {
        List<SlotDto> slots = slotService.searchSlots(facilityId, zoneId, status, slotType);
        return ResponseEntity.ok(ApiResponse.success("Slots fetched successfully", slots));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get slot by ID")
    public ResponseEntity<ApiResponse<SlotDto>> getSlotById(@PathVariable Long id) {
        SlotDto slot = slotService.getSlotById(id);
        return ResponseEntity.ok(ApiResponse.success("Slot details fetched successfully", slot));
    }

    @PostMapping
    @Operation(summary = "Admin: Create a new parking slot")
    public ResponseEntity<ApiResponse<SlotDto>> createSlot(@Valid @RequestBody SlotDto dto) {
        SlotDto created = slotService.createSlot(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Slot created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Admin: Update parking slot details")
    public ResponseEntity<ApiResponse<SlotDto>> updateSlot(@PathVariable Long id, @Valid @RequestBody SlotDto dto) {
        SlotDto updated = slotService.updateSlot(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Slot updated successfully", updated));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update slot occupancy status (AVAILABLE, OCCUPIED, MAINTENANCE, etc.)")
    public ResponseEntity<ApiResponse<SlotDto>> updateSlotStatus(
            @PathVariable Long id,
            @Valid @RequestBody SlotStatusUpdateRequest request) {
        SlotDto updated = slotService.updateSlotStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Slot status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Deactivate parking slot")
    public ResponseEntity<ApiResponse<Void>> deleteSlot(@PathVariable Long id) {
        slotService.deleteSlot(id);
        return ResponseEntity.ok(ApiResponse.success("Slot deactivated successfully", null));
    }
}

package com.parkgrid.parking.controller;

import com.parkgrid.parking.dto.ApiResponse;
import com.parkgrid.parking.dto.ZoneDto;
import com.parkgrid.parking.service.ParkingZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking/zones")
@Tag(name = "Parking Zones", description = "Endpoints for creating and managing parking zones inside facilities")
public class ZoneController {

    private final ParkingZoneService zoneService;

    public ZoneController(ParkingZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping
    @Operation(summary = "Get parking zones with optional facility filter")
    public ResponseEntity<ApiResponse<List<ZoneDto>>> getZones(
            @RequestParam(required = false) Long facilityId,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<ZoneDto> zones = zoneService.getZones(facilityId, activeOnly);
        return ResponseEntity.ok(ApiResponse.success("Zones fetched successfully", zones));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get zone by ID")
    public ResponseEntity<ApiResponse<ZoneDto>> getZoneById(@PathVariable Long id) {
        ZoneDto zone = zoneService.getZoneById(id);
        return ResponseEntity.ok(ApiResponse.success("Zone details fetched successfully", zone));
    }

    @PostMapping
    @Operation(summary = "Admin: Create a new parking zone")
    public ResponseEntity<ApiResponse<ZoneDto>> createZone(@Valid @RequestBody ZoneDto dto) {
        ZoneDto created = zoneService.createZone(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Zone created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Admin: Update parking zone")
    public ResponseEntity<ApiResponse<ZoneDto>> updateZone(@PathVariable Long id, @Valid @RequestBody ZoneDto dto) {
        ZoneDto updated = zoneService.updateZone(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Zone updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Deactivate parking zone")
    public ResponseEntity<ApiResponse<Void>> deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.ok(ApiResponse.success("Zone deactivated successfully", null));
    }
}

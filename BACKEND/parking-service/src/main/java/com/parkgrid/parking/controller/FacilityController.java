package com.parkgrid.parking.controller;

import com.parkgrid.parking.dto.ApiResponse;
import com.parkgrid.parking.dto.FacilityAvailabilityDto;
import com.parkgrid.parking.dto.FacilityDto;
import com.parkgrid.parking.dto.ParkingStatisticsDto;
import com.parkgrid.parking.service.ParkingFacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking")
@Tag(name = "Facilities & Availability", description = "Endpoints for managing parking facilities, viewing real-time availability, and statistics")
public class FacilityController {

    private final ParkingFacilityService facilityService;

    public FacilityController(ParkingFacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping("/facilities")
    @Operation(summary = "Get list of all parking facilities")
    public ResponseEntity<ApiResponse<List<FacilityDto>>> getFacilities(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<FacilityDto> facilities = facilityService.getAllFacilities(activeOnly);
        return ResponseEntity.ok(ApiResponse.success("Facilities fetched successfully", facilities));
    }

    @GetMapping("/facilities/{id}")
    @Operation(summary = "Get facility details by ID")
    public ResponseEntity<ApiResponse<FacilityDto>> getFacilityById(@PathVariable Long id) {
        FacilityDto facility = facilityService.getFacilityById(id);
        return ResponseEntity.ok(ApiResponse.success("Facility details fetched successfully", facility));
    }

    @PostMapping("/facilities")
    @Operation(summary = "Admin: Create a new parking facility")
    public ResponseEntity<ApiResponse<FacilityDto>> createFacility(@Valid @RequestBody FacilityDto dto) {
        FacilityDto created = facilityService.createFacility(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Facility created successfully", created));
    }

    @PutMapping("/facilities/{id}")
    @Operation(summary = "Admin: Update parking facility details")
    public ResponseEntity<ApiResponse<FacilityDto>> updateFacility(@PathVariable Long id, @Valid @RequestBody FacilityDto dto) {
        FacilityDto updated = facilityService.updateFacility(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Facility updated successfully", updated));
    }

    @DeleteMapping("/facilities/{id}")
    @Operation(summary = "Admin: Deactivate parking facility")
    public ResponseEntity<ApiResponse<Void>> deleteFacility(@PathVariable Long id) {
        facilityService.deleteFacility(id);
        return ResponseEntity.ok(ApiResponse.success("Facility deactivated successfully", null));
    }

    @GetMapping("/availability")
    @Operation(summary = "Get live parking availability overview across all facilities and zones")
    public ResponseEntity<ApiResponse<List<FacilityAvailabilityDto>>> getAvailability() {
        List<FacilityAvailabilityDto> availability = facilityService.getAvailabilityOverview();
        return ResponseEntity.ok(ApiResponse.success("Live availability fetched successfully", availability));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get comprehensive parking statistics and occupancy metrics")
    public ResponseEntity<ApiResponse<ParkingStatisticsDto>> getStatistics() {
        ParkingStatisticsDto stats = facilityService.getStatistics();
        return ResponseEntity.ok(ApiResponse.success("Parking statistics fetched successfully", stats));
    }
}

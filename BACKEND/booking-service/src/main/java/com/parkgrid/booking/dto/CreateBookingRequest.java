package com.parkgrid.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateBookingRequest {

    @NotNull(message = "Facility ID is required")
    private Long facilityId;

    @NotNull(message = "Zone ID is required")
    private Long zoneId;

    @NotNull(message = "Slot ID is required")
    private Long slotId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "Expected end time is required")
    private LocalDateTime expectedEndTime;

    @NotBlank(message = "Vehicle license plate number is required")
    private String vehicleNumber;

    private String customerName;

    public CreateBookingRequest() {}

    public CreateBookingRequest(Long facilityId, Long zoneId, Long slotId, LocalDateTime startTime, LocalDateTime expectedEndTime, String vehicleNumber, String customerName) {
        this.facilityId = facilityId;
        this.zoneId = zoneId;
        this.slotId = slotId;
        this.startTime = startTime;
        this.expectedEndTime = expectedEndTime;
        this.vehicleNumber = vehicleNumber;
        this.customerName = customerName;
    }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getExpectedEndTime() { return expectedEndTime; }
    public void setExpectedEndTime(LocalDateTime expectedEndTime) { this.expectedEndTime = expectedEndTime; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}

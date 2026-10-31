package com.parkgrid.booking.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SlotAvailabilityDto {
    private Long facilityId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> unavailableSlotIds;
    private int totalUnavailable;

    public SlotAvailabilityDto() {}

    public SlotAvailabilityDto(Long facilityId, LocalDateTime startTime, LocalDateTime endTime, List<Long> unavailableSlotIds) {
        this.facilityId = facilityId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.unavailableSlotIds = unavailableSlotIds;
        this.totalUnavailable = unavailableSlotIds != null ? unavailableSlotIds.size() : 0;
    }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public List<Long> getUnavailableSlotIds() { return unavailableSlotIds; }
    public void setUnavailableSlotIds(List<Long> unavailableSlotIds) {
        this.unavailableSlotIds = unavailableSlotIds;
        this.totalUnavailable = unavailableSlotIds != null ? unavailableSlotIds.size() : 0;
    }

    public int getTotalUnavailable() { return totalUnavailable; }
    public void setTotalUnavailable(int totalUnavailable) { this.totalUnavailable = totalUnavailable; }
}

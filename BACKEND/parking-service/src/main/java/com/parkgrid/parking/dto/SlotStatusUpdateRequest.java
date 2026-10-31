package com.parkgrid.parking.dto;

import com.parkgrid.parking.entity.SlotStatus;
import jakarta.validation.constraints.NotNull;

public class SlotStatusUpdateRequest {
    @NotNull(message = "Status cannot be null")
    private SlotStatus status;

    public SlotStatusUpdateRequest() {}

    public SlotStatusUpdateRequest(SlotStatus status) {
        this.status = status;
    }

    public SlotStatus getStatus() { return status; }
    public void setStatus(SlotStatus status) { this.status = status; }
}

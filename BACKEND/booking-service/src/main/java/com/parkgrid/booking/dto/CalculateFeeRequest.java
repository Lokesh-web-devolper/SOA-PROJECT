package com.parkgrid.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CalculateFeeRequest {
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private BigDecimal hourlyRate;
    private Long facilityId;

    public CalculateFeeRequest() {}

    public CalculateFeeRequest(LocalDateTime entryTime, LocalDateTime exitTime, BigDecimal hourlyRate, Long facilityId) {
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.hourlyRate = hourlyRate;
        this.facilityId = facilityId;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private BigDecimal hourlyRate;
        private Long facilityId;

        public Builder entryTime(LocalDateTime entryTime) { this.entryTime = entryTime; return this; }
        public Builder exitTime(LocalDateTime exitTime) { this.exitTime = exitTime; return this; }
        public Builder hourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; return this; }
        public Builder facilityId(Long facilityId) { this.facilityId = facilityId; return this; }

        public CalculateFeeRequest build() {
            return new CalculateFeeRequest(entryTime, exitTime, hourlyRate, facilityId);
        }
    }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }
}

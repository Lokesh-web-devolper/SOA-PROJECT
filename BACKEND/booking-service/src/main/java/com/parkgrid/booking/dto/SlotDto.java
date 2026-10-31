package com.parkgrid.booking.dto;

import java.math.BigDecimal;

public class SlotDto {
    private Long id;
    private String slotNumber;
    private Long facilityId;
    private String facilityName;
    private Long zoneId;
    private String zoneName;
    private String slotType;
    private String status;
    private BigDecimal hourlyRate;
    private boolean active = true;

    public SlotDto() {}

    public SlotDto(Long id, String slotNumber, Long facilityId, String facilityName, Long zoneId, String zoneName, String slotType, String status, BigDecimal hourlyRate, boolean active) {
        this.id = id;
        this.slotNumber = slotNumber;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.slotType = slotType;
        this.status = status;
        this.hourlyRate = hourlyRate;
        this.active = active;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String slotNumber;
        private Long facilityId;
        private String facilityName;
        private Long zoneId;
        private String zoneName;
        private String slotType;
        private String status;
        private BigDecimal hourlyRate;
        private boolean active = true;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder slotNumber(String slotNumber) { this.slotNumber = slotNumber; return this; }
        public Builder facilityId(Long facilityId) { this.facilityId = facilityId; return this; }
        public Builder facilityName(String facilityName) { this.facilityName = facilityName; return this; }
        public Builder zoneId(Long zoneId) { this.zoneId = zoneId; return this; }
        public Builder zoneName(String zoneName) { this.zoneName = zoneName; return this; }
        public Builder slotType(String slotType) { this.slotType = slotType; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder hourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; return this; }
        public Builder active(boolean active) { this.active = active; return this; }

        public SlotDto build() {
            return new SlotDto(id, slotNumber, facilityId, facilityName, zoneId, zoneName, slotType, status, hourlyRate, active);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public String getSlotType() { return slotType; }
    public void setSlotType(String slotType) { this.slotType = slotType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

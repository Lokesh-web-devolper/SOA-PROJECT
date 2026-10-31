package com.parkgrid.parking.dto;

import com.parkgrid.parking.entity.SlotStatus;
import com.parkgrid.parking.entity.SlotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SlotDto {
    private Long id;

    @NotBlank(message = "Slot number is required")
    private String slotNumber;

    @NotNull(message = "Facility ID is required")
    private Long facilityId;

    private String facilityName;

    @NotNull(message = "Zone ID is required")
    private Long zoneId;

    private String zoneName;

    @NotNull(message = "Slot type is required")
    private SlotType slotType;

    @NotNull(message = "Slot status is required")
    private SlotStatus status;

    @NotNull(message = "Hourly rate is required")
    private BigDecimal hourlyRate;

    private boolean active = true;
    private Long version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SlotDto() {}

    public SlotDto(Long id, String slotNumber, Long facilityId, String facilityName, Long zoneId, String zoneName, SlotType slotType, SlotStatus status, BigDecimal hourlyRate, boolean active, Long version, LocalDateTime createdAt, LocalDateTime updatedAt) {
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
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String slotNumber;
        private Long facilityId;
        private String facilityName;
        private Long zoneId;
        private String zoneName;
        private SlotType slotType;
        private SlotStatus status;
        private BigDecimal hourlyRate;
        private boolean active = true;
        private Long version;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder slotNumber(String slotNumber) { this.slotNumber = slotNumber; return this; }
        public Builder facilityId(Long facilityId) { this.facilityId = facilityId; return this; }
        public Builder facilityName(String facilityName) { this.facilityName = facilityName; return this; }
        public Builder zoneId(Long zoneId) { this.zoneId = zoneId; return this; }
        public Builder zoneName(String zoneName) { this.zoneName = zoneName; return this; }
        public Builder slotType(SlotType slotType) { this.slotType = slotType; return this; }
        public Builder status(SlotStatus status) { this.status = status; return this; }
        public Builder hourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder version(Long version) { this.version = version; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SlotDto build() {
            return new SlotDto(id, slotNumber, facilityId, facilityName, zoneId, zoneName, slotType, status, hourlyRate, active, version, createdAt, updatedAt);
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

    public SlotType getSlotType() { return slotType; }
    public void setSlotType(SlotType slotType) { this.slotType = slotType; }

    public SlotStatus getStatus() { return status; }
    public void setStatus(SlotStatus status) { this.status = status; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

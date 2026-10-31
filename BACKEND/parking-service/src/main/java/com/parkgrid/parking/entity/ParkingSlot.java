package com.parkgrid.parking.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_slots", indexes = {
        @Index(name = "idx_slot_zone", columnList = "zoneId"),
        @Index(name = "idx_slot_facility", columnList = "facilityId"),
        @Index(name = "idx_slot_status", columnList = "status")
})
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String slotNumber;

    @Column(nullable = false)
    private Long facilityId;

    @Column(nullable = false)
    private Long zoneId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SlotType slotType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SlotStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(nullable = false)
    private boolean active = true;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ParkingSlot() {}

    public ParkingSlot(Long id, String slotNumber, Long facilityId, Long zoneId, SlotType slotType, SlotStatus status, BigDecimal hourlyRate, boolean active, Long version, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.slotNumber = slotNumber;
        this.facilityId = facilityId;
        this.zoneId = zoneId;
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
        private Long zoneId;
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
        public Builder zoneId(Long zoneId) { this.zoneId = zoneId; return this; }
        public Builder slotType(SlotType slotType) { this.slotType = slotType; return this; }
        public Builder status(SlotStatus status) { this.status = status; return this; }
        public Builder hourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder version(Long version) { this.version = version; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ParkingSlot build() {
            return new ParkingSlot(id, slotNumber, facilityId, zoneId, slotType, status, hourlyRate, active, version, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }

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

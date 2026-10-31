package com.parkgrid.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ZoneDto {
    private Long id;

    @NotBlank(message = "Zone name is required")
    private String name;

    @NotNull(message = "Facility ID is required")
    private Long facilityId;

    private String facilityName;

    @NotNull(message = "Total slots is required")
    private Integer totalSlots;

    private Integer availableSlots;
    private Integer occupiedSlots;
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ZoneDto() {}

    public ZoneDto(Long id, String name, Long facilityId, String facilityName, Integer totalSlots, Integer availableSlots, Integer occupiedSlots, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.totalSlots = totalSlots;
        this.availableSlots = availableSlots;
        this.occupiedSlots = occupiedSlots;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private Long facilityId;
        private String facilityName;
        private Integer totalSlots;
        private Integer availableSlots;
        private Integer occupiedSlots;
        private boolean active = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder facilityId(Long facilityId) { this.facilityId = facilityId; return this; }
        public Builder facilityName(String facilityName) { this.facilityName = facilityName; return this; }
        public Builder totalSlots(Integer totalSlots) { this.totalSlots = totalSlots; return this; }
        public Builder availableSlots(Integer availableSlots) { this.availableSlots = availableSlots; return this; }
        public Builder occupiedSlots(Integer occupiedSlots) { this.occupiedSlots = occupiedSlots; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ZoneDto build() {
            return new ZoneDto(id, name, facilityId, facilityName, totalSlots, availableSlots, occupiedSlots, active, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public Integer getTotalSlots() { return totalSlots; }
    public void setTotalSlots(Integer totalSlots) { this.totalSlots = totalSlots; }

    public Integer getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(Integer availableSlots) { this.availableSlots = availableSlots; }

    public Integer getOccupiedSlots() { return occupiedSlots; }
    public void setOccupiedSlots(Integer occupiedSlots) { this.occupiedSlots = occupiedSlots; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

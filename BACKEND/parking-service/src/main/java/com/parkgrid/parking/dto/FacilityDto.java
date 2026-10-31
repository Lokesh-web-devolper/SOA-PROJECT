package com.parkgrid.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class FacilityDto {
    private Long id;

    @NotBlank(message = "Facility name is required")
    @Size(min = 2, max = 150)
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private String description;
    private Double latitude;
    private Double longitude;
    private boolean active = true;

    private Integer totalSlots;
    private Integer availableSlots;
    private Integer occupiedSlots;
    private Double startingPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FacilityDto() {}

    public FacilityDto(Long id, String name, String address, String description, Double latitude, Double longitude, boolean active, Integer totalSlots, Integer availableSlots, Integer occupiedSlots, Double startingPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = active;
        this.totalSlots = totalSlots;
        this.availableSlots = availableSlots;
        this.occupiedSlots = occupiedSlots;
        this.startingPrice = startingPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String address;
        private String description;
        private Double latitude;
        private Double longitude;
        private boolean active = true;
        private Integer totalSlots;
        private Integer availableSlots;
        private Integer occupiedSlots;
        private Double startingPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder latitude(Double latitude) { this.latitude = latitude; return this; }
        public Builder longitude(Double longitude) { this.longitude = longitude; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder totalSlots(Integer totalSlots) { this.totalSlots = totalSlots; return this; }
        public Builder availableSlots(Integer availableSlots) { this.availableSlots = availableSlots; return this; }
        public Builder occupiedSlots(Integer occupiedSlots) { this.occupiedSlots = occupiedSlots; return this; }
        public Builder startingPrice(Double startingPrice) { this.startingPrice = startingPrice; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public FacilityDto build() {
            return new FacilityDto(id, name, address, description, latitude, longitude, active, totalSlots, availableSlots, occupiedSlots, startingPrice, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Integer getTotalSlots() { return totalSlots; }
    public void setTotalSlots(Integer totalSlots) { this.totalSlots = totalSlots; }

    public Integer getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(Integer availableSlots) { this.availableSlots = availableSlots; }

    public Integer getOccupiedSlots() { return occupiedSlots; }
    public void setOccupiedSlots(Integer occupiedSlots) { this.occupiedSlots = occupiedSlots; }

    public Double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(Double startingPrice) { this.startingPrice = startingPrice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

package com.parkgrid.parking.dto;

import java.util.List;

public class FacilityAvailabilityDto {
    private Long facilityId;
    private String facilityName;
    private String address;
    private int totalSlots;
    private int availableSlots;
    private int occupiedSlots;
    private double occupancyRate;
    private List<ZoneDto> zones;

    public FacilityAvailabilityDto() {}

    public FacilityAvailabilityDto(Long facilityId, String facilityName, String address, int totalSlots, int availableSlots, int occupiedSlots, double occupancyRate, List<ZoneDto> zones) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.address = address;
        this.totalSlots = totalSlots;
        this.availableSlots = availableSlots;
        this.occupiedSlots = occupiedSlots;
        this.occupancyRate = occupancyRate;
        this.zones = zones;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long facilityId;
        private String facilityName;
        private String address;
        private int totalSlots;
        private int availableSlots;
        private int occupiedSlots;
        private double occupancyRate;
        private List<ZoneDto> zones;

        public Builder facilityId(Long facilityId) { this.facilityId = facilityId; return this; }
        public Builder facilityName(String facilityName) { this.facilityName = facilityName; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder totalSlots(int totalSlots) { this.totalSlots = totalSlots; return this; }
        public Builder availableSlots(int availableSlots) { this.availableSlots = availableSlots; return this; }
        public Builder occupiedSlots(int occupiedSlots) { this.occupiedSlots = occupiedSlots; return this; }
        public Builder occupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; return this; }
        public Builder zones(List<ZoneDto> zones) { this.zones = zones; return this; }

        public FacilityAvailabilityDto build() {
            return new FacilityAvailabilityDto(facilityId, facilityName, address, totalSlots, availableSlots, occupiedSlots, occupancyRate, zones);
        }
    }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }

    public int getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(int availableSlots) { this.availableSlots = availableSlots; }

    public int getOccupiedSlots() { return occupiedSlots; }
    public void setOccupiedSlots(int occupiedSlots) { this.occupiedSlots = occupiedSlots; }

    public double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }

    public List<ZoneDto> getZones() { return zones; }
    public void setZones(List<ZoneDto> zones) { this.zones = zones; }
}

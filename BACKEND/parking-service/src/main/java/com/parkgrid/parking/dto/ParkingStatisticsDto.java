package com.parkgrid.parking.dto;

public class ParkingStatisticsDto {
    private long totalFacilities;
    private long totalZones;
    private long totalSlots;
    private long availableSlots;
    private long occupiedSlots;
    private long maintenanceSlots;
    private double occupancyPercentage;

    public ParkingStatisticsDto() {}

    public ParkingStatisticsDto(long totalFacilities, long totalZones, long totalSlots, long availableSlots, long occupiedSlots, long maintenanceSlots, double occupancyPercentage) {
        this.totalFacilities = totalFacilities;
        this.totalZones = totalZones;
        this.totalSlots = totalSlots;
        this.availableSlots = availableSlots;
        this.occupiedSlots = occupiedSlots;
        this.maintenanceSlots = maintenanceSlots;
        this.occupancyPercentage = occupancyPercentage;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalFacilities;
        private long totalZones;
        private long totalSlots;
        private long availableSlots;
        private long occupiedSlots;
        private long maintenanceSlots;
        private double occupancyPercentage;

        public Builder totalFacilities(long totalFacilities) { this.totalFacilities = totalFacilities; return this; }
        public Builder totalZones(long totalZones) { this.totalZones = totalZones; return this; }
        public Builder totalSlots(long totalSlots) { this.totalSlots = totalSlots; return this; }
        public Builder availableSlots(long availableSlots) { this.availableSlots = availableSlots; return this; }
        public Builder occupiedSlots(long occupiedSlots) { this.occupiedSlots = occupiedSlots; return this; }
        public Builder maintenanceSlots(long maintenanceSlots) { this.maintenanceSlots = maintenanceSlots; return this; }
        public Builder occupancyPercentage(double occupancyPercentage) { this.occupancyPercentage = occupancyPercentage; return this; }

        public ParkingStatisticsDto build() {
            return new ParkingStatisticsDto(totalFacilities, totalZones, totalSlots, availableSlots, occupiedSlots, maintenanceSlots, occupancyPercentage);
        }
    }

    public long getTotalFacilities() { return totalFacilities; }
    public void setTotalFacilities(long totalFacilities) { this.totalFacilities = totalFacilities; }

    public long getTotalZones() { return totalZones; }
    public void setTotalZones(long totalZones) { this.totalZones = totalZones; }

    public long getTotalSlots() { return totalSlots; }
    public void setTotalSlots(long totalSlots) { this.totalSlots = totalSlots; }

    public long getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(long availableSlots) { this.availableSlots = availableSlots; }

    public long getOccupiedSlots() { return occupiedSlots; }
    public void setOccupiedSlots(long occupiedSlots) { this.occupiedSlots = occupiedSlots; }

    public long getMaintenanceSlots() { return maintenanceSlots; }
    public void setMaintenanceSlots(long maintenanceSlots) { this.maintenanceSlots = maintenanceSlots; }

    public double getOccupancyPercentage() { return occupancyPercentage; }
    public void setOccupancyPercentage(double occupancyPercentage) { this.occupancyPercentage = occupancyPercentage; }
}

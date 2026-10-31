package com.parkgrid.billing.dto;

import java.math.BigDecimal;

public class CalculateFeeResponse {
    private long totalMinutes;
    private double billableHours;
    private BigDecimal hourlyRate;
    private BigDecimal totalAmount;
    private String calculationBreakdown;

    public CalculateFeeResponse() {}

    public CalculateFeeResponse(long totalMinutes, double billableHours, BigDecimal hourlyRate, BigDecimal totalAmount, String calculationBreakdown) {
        this.totalMinutes = totalMinutes;
        this.billableHours = billableHours;
        this.hourlyRate = hourlyRate;
        this.totalAmount = totalAmount;
        this.calculationBreakdown = calculationBreakdown;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalMinutes;
        private double billableHours;
        private BigDecimal hourlyRate;
        private BigDecimal totalAmount;
        private String calculationBreakdown;

        public Builder totalMinutes(long totalMinutes) { this.totalMinutes = totalMinutes; return this; }
        public Builder billableHours(double billableHours) { this.billableHours = billableHours; return this; }
        public Builder hourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder calculationBreakdown(String calculationBreakdown) { this.calculationBreakdown = calculationBreakdown; return this; }

        public CalculateFeeResponse build() {
            return new CalculateFeeResponse(totalMinutes, billableHours, hourlyRate, totalAmount, calculationBreakdown);
        }
    }

    public long getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(long totalMinutes) { this.totalMinutes = totalMinutes; }

    public double getBillableHours() { return billableHours; }
    public void setBillableHours(double billableHours) { this.billableHours = billableHours; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCalculationBreakdown() { return calculationBreakdown; }
    public void setCalculationBreakdown(String calculationBreakdown) { this.calculationBreakdown = calculationBreakdown; }
}

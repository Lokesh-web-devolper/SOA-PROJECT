package com.parkgrid.billing.dto;

import java.math.BigDecimal;
import java.util.List;

public class RevenueStatisticsDto {
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal weekRevenue;
    private BigDecimal monthRevenue;
    private long totalInvoices;
    private long paidInvoices;
    private List<DailyRevenuePoint> dailyRevenueTrend;
    private List<FacilityRevenuePoint> facilityBreakdown;

    public RevenueStatisticsDto() {}

    public RevenueStatisticsDto(BigDecimal totalRevenue, BigDecimal todayRevenue, BigDecimal weekRevenue, BigDecimal monthRevenue, long totalInvoices, long paidInvoices, List<DailyRevenuePoint> dailyRevenueTrend, List<FacilityRevenuePoint> facilityBreakdown) {
        this.totalRevenue = totalRevenue;
        this.todayRevenue = todayRevenue;
        this.weekRevenue = weekRevenue;
        this.monthRevenue = monthRevenue;
        this.totalInvoices = totalInvoices;
        this.paidInvoices = paidInvoices;
        this.dailyRevenueTrend = dailyRevenueTrend;
        this.facilityBreakdown = facilityBreakdown;
    }

    public static class DailyRevenuePoint {
        private String date;
        private BigDecimal amount;

        public DailyRevenuePoint() {}
        public DailyRevenuePoint(String date, BigDecimal amount) {
            this.date = date;
            this.amount = amount;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public static class FacilityRevenuePoint {
        private String facilityName;
        private BigDecimal amount;

        public FacilityRevenuePoint() {}
        public FacilityRevenuePoint(String facilityName, BigDecimal amount) {
            this.facilityName = facilityName;
            this.amount = amount;
        }

        public String getFacilityName() { return facilityName; }
        public void setFacilityName(String facilityName) { this.facilityName = facilityName; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BigDecimal totalRevenue;
        private BigDecimal todayRevenue;
        private BigDecimal weekRevenue;
        private BigDecimal monthRevenue;
        private long totalInvoices;
        private long paidInvoices;
        private List<DailyRevenuePoint> dailyRevenueTrend;
        private List<FacilityRevenuePoint> facilityBreakdown;

        public Builder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
        public Builder todayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; return this; }
        public Builder weekRevenue(BigDecimal weekRevenue) { this.weekRevenue = weekRevenue; return this; }
        public Builder monthRevenue(BigDecimal monthRevenue) { this.monthRevenue = monthRevenue; return this; }
        public Builder totalInvoices(long totalInvoices) { this.totalInvoices = totalInvoices; return this; }
        public Builder paidInvoices(long paidInvoices) { this.paidInvoices = paidInvoices; return this; }
        public Builder dailyRevenueTrend(List<DailyRevenuePoint> dailyRevenueTrend) { this.dailyRevenueTrend = dailyRevenueTrend; return this; }
        public Builder facilityBreakdown(List<FacilityRevenuePoint> facilityBreakdown) { this.facilityBreakdown = facilityBreakdown; return this; }

        public RevenueStatisticsDto build() {
            return new RevenueStatisticsDto(totalRevenue, todayRevenue, weekRevenue, monthRevenue, totalInvoices, paidInvoices, dailyRevenueTrend, facilityBreakdown);
        }
    }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }

    public BigDecimal getWeekRevenue() { return weekRevenue; }
    public void setWeekRevenue(BigDecimal weekRevenue) { this.weekRevenue = weekRevenue; }

    public BigDecimal getMonthRevenue() { return monthRevenue; }
    public void setMonthRevenue(BigDecimal monthRevenue) { this.monthRevenue = monthRevenue; }

    public long getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(long totalInvoices) { this.totalInvoices = totalInvoices; }

    public long getPaidInvoices() { return paidInvoices; }
    public void setPaidInvoices(long paidInvoices) { this.paidInvoices = paidInvoices; }

    public List<DailyRevenuePoint> getDailyRevenueTrend() { return dailyRevenueTrend; }
    public void setDailyRevenueTrend(List<DailyRevenuePoint> dailyRevenueTrend) { this.dailyRevenueTrend = dailyRevenueTrend; }

    public List<FacilityRevenuePoint> getFacilityBreakdown() { return facilityBreakdown; }
    public void setFacilityBreakdown(List<FacilityRevenuePoint> facilityBreakdown) { this.facilityBreakdown = facilityBreakdown; }
}

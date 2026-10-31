package com.parkgrid.billing.dto;

import com.parkgrid.billing.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceDto {
    private Long id;
    private String invoiceNumber;
    private Long bookingId;
    private String bookingReference;
    private Long userId;
    private String userEmail;
    private Long facilityId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Long parkingDurationMinutes;
    private Double billableHours;
    private BigDecimal hourlyRate;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;

    public InvoiceDto() {}

    public InvoiceDto(Long id, String invoiceNumber, Long bookingId, String bookingReference, Long userId, String userEmail, Long facilityId, LocalDateTime entryTime, LocalDateTime exitTime, Long parkingDurationMinutes, Double billableHours, BigDecimal hourlyRate, BigDecimal totalAmount, PaymentStatus paymentStatus, LocalDateTime createdAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.userEmail = userEmail;
        this.facilityId = facilityId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.parkingDurationMinutes = parkingDurationMinutes;
        this.billableHours = billableHours;
        this.hourlyRate = hourlyRate;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String invoiceNumber;
        private Long bookingId;
        private String bookingReference;
        private Long userId;
        private String userEmail;
        private Long facilityId;
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private Long parkingDurationMinutes;
        private Double billableHours;
        private BigDecimal hourlyRate;
        private BigDecimal totalAmount;
        private PaymentStatus paymentStatus;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public Builder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public Builder bookingReference(String bookingReference) { this.bookingReference = bookingReference; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public Builder facilityId(Long facilityId) { this.facilityId = facilityId; return this; }
        public Builder entryTime(LocalDateTime entryTime) { this.entryTime = entryTime; return this; }
        public Builder exitTime(LocalDateTime exitTime) { this.exitTime = exitTime; return this; }
        public Builder parkingDurationMinutes(Long parkingDurationMinutes) { this.parkingDurationMinutes = parkingDurationMinutes; return this; }
        public Builder billableHours(Double billableHours) { this.billableHours = billableHours; return this; }
        public Builder hourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public InvoiceDto build() {
            return new InvoiceDto(id, invoiceNumber, bookingId, bookingReference, userId, userEmail, facilityId, entryTime, exitTime, parkingDurationMinutes, billableHours, hourlyRate, totalAmount, paymentStatus, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public Long getParkingDurationMinutes() { return parkingDurationMinutes; }
    public void setParkingDurationMinutes(Long parkingDurationMinutes) { this.parkingDurationMinutes = parkingDurationMinutes; }

    public Double getBillableHours() { return billableHours; }
    public void setBillableHours(Double billableHours) { this.billableHours = billableHours; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

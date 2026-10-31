package com.parkgrid.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateInvoiceRequest {
    private Long bookingId;
    private String bookingReference;
    private Long userId;
    private String userEmail;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private BigDecimal hourlyRate;
    private Long facilityId;

    public CreateInvoiceRequest() {}

    public CreateInvoiceRequest(Long bookingId, String bookingReference, Long userId, String userEmail, LocalDateTime entryTime, LocalDateTime exitTime, BigDecimal hourlyRate, Long facilityId) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.userEmail = userEmail;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.hourlyRate = hourlyRate;
        this.facilityId = facilityId;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long bookingId;
        private String bookingReference;
        private Long userId;
        private String userEmail;
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private BigDecimal hourlyRate;
        private Long facilityId;

        public Builder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public Builder bookingReference(String bookingReference) { this.bookingReference = bookingReference; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public Builder entryTime(LocalDateTime entryTime) { this.entryTime = entryTime; return this; }
        public Builder exitTime(LocalDateTime exitTime) { this.exitTime = exitTime; return this; }
        public Builder hourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; return this; }
        public Builder facilityId(Long facilityId) { this.facilityId = facilityId; return this; }

        public CreateInvoiceRequest build() {
            return new CreateInvoiceRequest(bookingId, bookingReference, userId, userEmail, entryTime, exitTime, hourlyRate, facilityId);
        }
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }
}

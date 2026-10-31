package com.parkgrid.billing.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateInvoiceRequest {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    private String bookingReference;

    @NotNull(message = "User ID is required")
    private Long userId;

    private String userEmail;
    private Long facilityId;

    @NotNull(message = "Entry time is required")
    private LocalDateTime entryTime;

    @NotNull(message = "Exit time is required")
    private LocalDateTime exitTime;

    private BigDecimal hourlyRate;

    public CreateInvoiceRequest() {}

    public CreateInvoiceRequest(Long bookingId, String bookingReference, Long userId, String userEmail, Long facilityId, LocalDateTime entryTime, LocalDateTime exitTime, BigDecimal hourlyRate) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.userEmail = userEmail;
        this.facilityId = facilityId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.hourlyRate = hourlyRate;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long bookingId;
        private String bookingReference;
        private Long userId;
        private String userEmail;
        private Long facilityId;
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private BigDecimal hourlyRate;

        public Builder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public Builder bookingReference(String bookingReference) { this.bookingReference = bookingReference; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public Builder facilityId(Long facilityId) { this.facilityId = facilityId; return this; }
        public Builder entryTime(LocalDateTime entryTime) { this.entryTime = entryTime; return this; }
        public Builder exitTime(LocalDateTime exitTime) { this.exitTime = exitTime; return this; }
        public Builder hourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; return this; }

        public CreateInvoiceRequest build() {
            return new CreateInvoiceRequest(bookingId, bookingReference, userId, userEmail, facilityId, entryTime, exitTime, hourlyRate);
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

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
}

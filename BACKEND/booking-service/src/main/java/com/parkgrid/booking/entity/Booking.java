package com.parkgrid.booking.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_booking_user", columnList = "userId"),
        @Index(name = "idx_booking_slot", columnList = "slotId"),
        @Index(name = "idx_booking_status", columnList = "bookingStatus"),
        @Index(name = "idx_booking_ref", columnList = "bookingReference", unique = true)
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String bookingReference;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 120)
    private String userEmail;

    @Column(length = 120)
    private String customerName;

    @Column(length = 40)
    private String vehicleNumber;

    @Column(nullable = false)
    private Long facilityId;

    @Column(length = 150)
    private String facilityName;

    @Column(nullable = false)
    private Long zoneId;

    @Column(length = 100)
    private String zoneName;

    @Column(nullable = false)
    private Long slotId;

    @Column(nullable = false, length = 30)
    private String slotNumber;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime expectedEndTime;

    private LocalDateTime actualEntryTime;
    private LocalDateTime actualExitTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus bookingStatus;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Booking() {}

    public Booking(Long id, String bookingReference, Long userId, String userEmail, String customerName, String vehicleNumber, Long facilityId, String facilityName, Long zoneId, String zoneName, Long slotId, String slotNumber, LocalDateTime startTime, LocalDateTime expectedEndTime, LocalDateTime actualEntryTime, LocalDateTime actualExitTime, BookingStatus bookingStatus, BigDecimal hourlyRate, BigDecimal estimatedAmount, BigDecimal finalAmount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.userEmail = userEmail;
        this.customerName = customerName;
        this.vehicleNumber = vehicleNumber;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.startTime = startTime;
        this.expectedEndTime = expectedEndTime;
        this.actualEntryTime = actualEntryTime;
        this.actualExitTime = actualExitTime;
        this.bookingStatus = bookingStatus;
        this.hourlyRate = hourlyRate;
        this.estimatedAmount = estimatedAmount;
        this.finalAmount = finalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String bookingReference;
        private Long userId;
        private String userEmail;
        private String customerName;
        private String vehicleNumber;
        private Long facilityId;
        private String facilityName;
        private Long zoneId;
        private String zoneName;
        private Long slotId;
        private String slotNumber;
        private LocalDateTime startTime;
        private LocalDateTime expectedEndTime;
        private LocalDateTime actualEntryTime;
        private LocalDateTime actualExitTime;
        private BookingStatus bookingStatus;
        private BigDecimal hourlyRate;
        private BigDecimal estimatedAmount;
        private BigDecimal finalAmount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder bookingReference(String bookingReference) { this.bookingReference = bookingReference; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public Builder customerName(String customerName) { this.customerName = customerName; return this; }
        public Builder vehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; return this; }
        public Builder facilityId(Long facilityId) { this.facilityId = facilityId; return this; }
        public Builder facilityName(String facilityName) { this.facilityName = facilityName; return this; }
        public Builder zoneId(Long zoneId) { this.zoneId = zoneId; return this; }
        public Builder zoneName(String zoneName) { this.zoneName = zoneName; return this; }
        public Builder slotId(Long slotId) { this.slotId = slotId; return this; }
        public Builder slotNumber(String slotNumber) { this.slotNumber = slotNumber; return this; }
        public Builder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public Builder expectedEndTime(LocalDateTime expectedEndTime) { this.expectedEndTime = expectedEndTime; return this; }
        public Builder actualEntryTime(LocalDateTime actualEntryTime) { this.actualEntryTime = actualEntryTime; return this; }
        public Builder actualExitTime(LocalDateTime actualExitTime) { this.actualExitTime = actualExitTime; return this; }
        public Builder bookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; return this; }
        public Builder hourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; return this; }
        public Builder estimatedAmount(BigDecimal estimatedAmount) { this.estimatedAmount = estimatedAmount; return this; }
        public Builder finalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Booking build() {
            return new Booking(id, bookingReference, userId, userEmail, customerName, vehicleNumber, facilityId, facilityName, zoneId, zoneName, slotId, slotNumber, startTime, expectedEndTime, actualEntryTime, actualExitTime, bookingStatus, hourlyRate, estimatedAmount, finalAmount, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getExpectedEndTime() { return expectedEndTime; }
    public void setExpectedEndTime(LocalDateTime expectedEndTime) { this.expectedEndTime = expectedEndTime; }

    public LocalDateTime getActualEntryTime() { return actualEntryTime; }
    public void setActualEntryTime(LocalDateTime actualEntryTime) { this.actualEntryTime = actualEntryTime; }

    public LocalDateTime getActualExitTime() { return actualExitTime; }
    public void setActualExitTime(LocalDateTime actualExitTime) { this.actualExitTime = actualExitTime; }

    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public BigDecimal getEstimatedAmount() { return estimatedAmount; }
    public void setEstimatedAmount(BigDecimal estimatedAmount) { this.estimatedAmount = estimatedAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

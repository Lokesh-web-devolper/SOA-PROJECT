package com.parkgrid.booking.repository;

import com.parkgrid.booking.entity.Booking;
import com.parkgrid.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countByBookingStatus(BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.slotId = :slotId " +
           "AND b.bookingStatus IN ('CONFIRMED', 'ACTIVE', 'PENDING') " +
           "AND (b.startTime < :expectedEndTime AND b.expectedEndTime > :startTime)")
    long countOverlappingBookings(@Param("slotId") Long slotId,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("expectedEndTime") LocalDateTime expectedEndTime);

    @Query("SELECT b.slotId FROM Booking b WHERE b.facilityId = :facilityId " +
           "AND b.bookingStatus IN ('CONFIRMED', 'ACTIVE', 'PENDING') " +
           "AND (b.startTime < :expectedEndTime AND b.expectedEndTime > :startTime)")
    List<Long> findOverlappingSlotIds(@Param("facilityId") Long facilityId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("expectedEndTime") LocalDateTime expectedEndTime);

    @Query("SELECT b FROM Booking b WHERE b.slotId = :slotId " +
           "AND b.bookingStatus IN ('CONFIRMED', 'ACTIVE', 'PENDING') " +
           "AND (b.startTime < :expectedEndTime AND b.expectedEndTime > :startTime)")
    List<Booking> findOverlappingBookings(@Param("slotId") Long slotId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("expectedEndTime") LocalDateTime expectedEndTime);

    @Query("SELECT b FROM Booking b WHERE " +
           "(:facilityId IS NULL OR b.facilityId = :facilityId) AND " +
           "(:status IS NULL OR b.bookingStatus = :status) " +
           "ORDER BY b.createdAt DESC")
    List<Booking> searchBookings(@Param("facilityId") Long facilityId,
                                 @Param("status") BookingStatus status);
}

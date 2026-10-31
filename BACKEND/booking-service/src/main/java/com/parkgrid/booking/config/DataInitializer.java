package com.parkgrid.booking.config;

import com.parkgrid.booking.entity.Booking;
import com.parkgrid.booking.entity.BookingStatus;
import com.parkgrid.booking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final BookingRepository bookingRepository;

    public DataInitializer(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void run(String... args) {
        if (bookingRepository.count() > 0) {
            log.info("Booking sample data already initialized.");
            return;
        }

        log.info("Initializing sample bookings...");

        bookingRepository.save(Booking.builder()
                .bookingReference("PG-20261028-1001A")
                .userId(2L)
                .userEmail("customer@parkgrid.com")
                .customerName("Alex Rivera")
                .vehicleNumber("CA-7XYZ99")
                .facilityId(1L)
                .facilityName("ParkGrid Central Plaza")
                .zoneId(1L)
                .zoneName("Zone A - Ground Level")
                .slotId(1L)
                .slotNumber("A-101")
                .startTime(LocalDateTime.now().minusDays(2).withHour(9).withMinute(0))
                .expectedEndTime(LocalDateTime.now().minusDays(2).withHour(12).withMinute(0))
                .actualEntryTime(LocalDateTime.now().minusDays(2).withHour(9).withMinute(5))
                .actualExitTime(LocalDateTime.now().minusDays(2).withHour(11).withMinute(50))
                .bookingStatus(BookingStatus.COMPLETED)
                .hourlyRate(new BigDecimal("15.00"))
                .estimatedAmount(new BigDecimal("45.00"))
                .finalAmount(new BigDecimal("45.00"))
                .build());

        bookingRepository.save(Booking.builder()
                .bookingReference("PG-20261030-2002B")
                .userId(2L)
                .userEmail("customer@parkgrid.com")
                .customerName("Alex Rivera")
                .vehicleNumber("CA-7XYZ99")
                .facilityId(1L)
                .facilityName("ParkGrid Central Plaza")
                .zoneId(1L)
                .zoneName("Zone A - Ground Level")
                .slotId(2L)
                .slotNumber("A-102")
                .startTime(LocalDateTime.now().plusHours(1))
                .expectedEndTime(LocalDateTime.now().plusHours(3))
                .bookingStatus(BookingStatus.CONFIRMED)
                .hourlyRate(new BigDecimal("15.00"))
                .estimatedAmount(new BigDecimal("30.00"))
                .build());

        log.info("Sample bookings initialized successfully.");
    }
}

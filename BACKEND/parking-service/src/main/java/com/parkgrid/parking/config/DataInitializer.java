package com.parkgrid.parking.config;

import com.parkgrid.parking.entity.*;
import com.parkgrid.parking.repository.ParkingFacilityRepository;
import com.parkgrid.parking.repository.ParkingSlotRepository;
import com.parkgrid.parking.repository.ParkingZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ParkingFacilityRepository facilityRepository;
    private final ParkingZoneRepository zoneRepository;
    private final ParkingSlotRepository slotRepository;

    public DataInitializer(ParkingFacilityRepository facilityRepository, ParkingZoneRepository zoneRepository, ParkingSlotRepository slotRepository) {
        this.facilityRepository = facilityRepository;
        this.zoneRepository = zoneRepository;
        this.slotRepository = slotRepository;
    }

    @Override
    public void run(String... args) {
        if (facilityRepository.count() > 0) {
            log.info("Parking data already initialized.");
            return;
        }

        log.info("Initializing parking facilities, zones, and slots...");

        ParkingFacility f1 = facilityRepository.save(ParkingFacility.builder()
                .name("ParkGrid Central Plaza")
                .address("100 Financial Way, Downtown")
                .description("Multi-level covered parking facility equipped with ultra-fast EV chargers and 24/7 security surveillance.")
                .latitude(37.7749)
                .longitude(-122.4194)
                .active(true)
                .build());

        ParkingZone f1z1 = zoneRepository.save(ParkingZone.builder()
                .name("Zone A - Ground Level")
                .facilityId(f1.getId())
                .totalSlots(8)
                .active(true)
                .build());

        ParkingZone f1z2 = zoneRepository.save(ParkingZone.builder()
                .name("Zone B - EV Charging Hub")
                .facilityId(f1.getId())
                .totalSlots(6)
                .active(true)
                .build());

        ParkingFacility f2 = facilityRepository.save(ParkingFacility.builder()
                .name("Silicon Tech Park Garage")
                .address("500 Innovation Boulevard, Tech District")
                .description("Smart parking garage with automated license plate recognition and accessible priority slots.")
                .latitude(37.7833)
                .longitude(-122.4167)
                .active(true)
                .build());

        ParkingZone f2z1 = zoneRepository.save(ParkingZone.builder()
                .name("Zone 1 - Premium Covered")
                .facilityId(f2.getId())
                .totalSlots(8)
                .active(true)
                .build());

        ParkingZone f2z2 = zoneRepository.save(ParkingZone.builder()
                .name("Zone 2 - Rooftop Economy")
                .facilityId(f2.getId())
                .totalSlots(6)
                .active(true)
                .build());

        ParkingFacility f3 = facilityRepository.save(ParkingFacility.builder()
                .name("Metro Harbor Station Parking")
                .address("42 Marina Boulevard, Harbor Front")
                .description("High-capacity public park-and-ride facility near major railway and ferry terminals.")
                .latitude(37.8080)
                .longitude(-122.4177)
                .active(true)
                .build());

        ParkingZone f3z1 = zoneRepository.save(ParkingZone.builder()
                .name("North Wing Express")
                .facilityId(f3.getId())
                .totalSlots(8)
                .active(true)
                .build());

        List<ParkingSlot> slots = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            SlotType type = (i == 1) ? SlotType.ACCESSIBLE : SlotType.STANDARD;
            SlotStatus status = (i == 3) ? SlotStatus.OCCUPIED : SlotStatus.AVAILABLE;
            slots.add(ParkingSlot.builder()
                    .slotNumber("A-" + (100 + i))
                    .facilityId(f1.getId())
                    .zoneId(f1z1.getId())
                    .slotType(type)
                    .status(status)
                    .hourlyRate(new BigDecimal("15.00"))
                    .active(true)
                    .build());
        }

        for (int i = 1; i <= 6; i++) {
            slots.add(ParkingSlot.builder()
                    .slotNumber("B-" + (200 + i))
                    .facilityId(f1.getId())
                    .zoneId(f1z2.getId())
                    .slotType(SlotType.EV)
                    .status(i == 2 ? SlotStatus.OCCUPIED : SlotStatus.AVAILABLE)
                    .hourlyRate(new BigDecimal("22.00"))
                    .active(true)
                    .build());
        }

        for (int i = 1; i <= 8; i++) {
            SlotType type = (i == 1) ? SlotType.ACCESSIBLE : (i % 3 == 0 ? SlotType.EV : SlotType.STANDARD);
            slots.add(ParkingSlot.builder()
                    .slotNumber("S1-" + (10 + i))
                    .facilityId(f2.getId())
                    .zoneId(f2z1.getId())
                    .slotType(type)
                    .status(i == 4 ? SlotStatus.OCCUPIED : SlotStatus.AVAILABLE)
                    .hourlyRate(new BigDecimal("12.50"))
                    .active(true)
                    .build());
        }

        for (int i = 1; i <= 6; i++) {
            slots.add(ParkingSlot.builder()
                    .slotNumber("S2-" + (20 + i))
                    .facilityId(f2.getId())
                    .zoneId(f2z2.getId())
                    .slotType(SlotType.STANDARD)
                    .status(SlotStatus.AVAILABLE)
                    .hourlyRate(new BigDecimal("9.00"))
                    .active(true)
                    .build());
        }

        for (int i = 1; i <= 8; i++) {
            slots.add(ParkingSlot.builder()
                    .slotNumber("NW-" + (30 + i))
                    .facilityId(f3.getId())
                    .zoneId(f3z1.getId())
                    .slotType(i == 1 ? SlotType.ACCESSIBLE : SlotType.STANDARD)
                    .status(i == 1 ? SlotStatus.OCCUPIED : SlotStatus.AVAILABLE)
                    .hourlyRate(new BigDecimal("8.00"))
                    .active(true)
                    .build());
        }

        slotRepository.saveAll(slots);
        log.info("Initialized {} parking slots across 3 facilities.", slots.size());
    }
}

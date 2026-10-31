package com.parkgrid.parking.service;

import com.parkgrid.parking.dto.ZoneDto;
import com.parkgrid.parking.entity.ParkingFacility;
import com.parkgrid.parking.entity.ParkingSlot;
import com.parkgrid.parking.entity.ParkingZone;
import com.parkgrid.parking.entity.SlotStatus;
import com.parkgrid.parking.exception.ResourceNotFoundException;
import com.parkgrid.parking.repository.ParkingFacilityRepository;
import com.parkgrid.parking.repository.ParkingSlotRepository;
import com.parkgrid.parking.repository.ParkingZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingZoneService {

    private final ParkingZoneRepository zoneRepository;
    private final ParkingFacilityRepository facilityRepository;
    private final ParkingSlotRepository slotRepository;

    public ParkingZoneService(ParkingZoneRepository zoneRepository, ParkingFacilityRepository facilityRepository, ParkingSlotRepository slotRepository) {
        this.zoneRepository = zoneRepository;
        this.facilityRepository = facilityRepository;
        this.slotRepository = slotRepository;
    }

    public List<ZoneDto> getZones(Long facilityId, boolean activeOnly) {
        List<ParkingZone> zones;
        if (facilityId != null) {
            zones = activeOnly ? zoneRepository.findByFacilityIdAndActiveTrue(facilityId) : zoneRepository.findByFacilityId(facilityId);
        } else {
            zones = activeOnly ? zoneRepository.findByActiveTrue() : zoneRepository.findAll();
        }

        return zones.stream().map(this::enrichZoneDto).collect(Collectors.toList());
    }

    public ZoneDto getZoneById(Long id) {
        ParkingZone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + id));
        return enrichZoneDto(zone);
    }

    @Transactional
    public ZoneDto createZone(ZoneDto dto) {
        if (!facilityRepository.existsById(dto.getFacilityId())) {
            throw new ResourceNotFoundException("Facility not found with id: " + dto.getFacilityId());
        }

        ParkingZone zone = ParkingZone.builder()
                .name(dto.getName().trim())
                .facilityId(dto.getFacilityId())
                .totalSlots(dto.getTotalSlots())
                .active(dto.isActive())
                .build();

        return enrichZoneDto(zoneRepository.save(zone));
    }

    @Transactional
    public ZoneDto updateZone(Long id, ZoneDto dto) {
        ParkingZone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + id));

        zone.setName(dto.getName().trim());
        if (dto.getFacilityId() != null) {
            zone.setFacilityId(dto.getFacilityId());
        }
        if (dto.getTotalSlots() != null) {
            zone.setTotalSlots(dto.getTotalSlots());
        }
        zone.setActive(dto.isActive());

        return enrichZoneDto(zoneRepository.save(zone));
    }

    @Transactional
    public void deleteZone(Long id) {
        ParkingZone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + id));
        zone.setActive(false);
        zoneRepository.save(zone);
    }

    private ZoneDto enrichZoneDto(ParkingZone zone) {
        List<ParkingSlot> slots = slotRepository.findByZoneId(zone.getId());
        int available = (int) slots.stream().filter(s -> s.getStatus() == SlotStatus.AVAILABLE && s.isActive()).count();
        int occupied = (int) slots.stream().filter(s -> s.getStatus() == SlotStatus.OCCUPIED).count();

        String facilityName = facilityRepository.findById(zone.getFacilityId())
                .map(ParkingFacility::getName)
                .orElse("Unknown Facility");

        return ZoneDto.builder()
                .id(zone.getId())
                .name(zone.getName())
                .facilityId(zone.getFacilityId())
                .facilityName(facilityName)
                .totalSlots(zone.getTotalSlots())
                .availableSlots(available)
                .occupiedSlots(occupied)
                .active(zone.isActive())
                .createdAt(zone.getCreatedAt())
                .updatedAt(zone.getUpdatedAt())
                .build();
    }
}

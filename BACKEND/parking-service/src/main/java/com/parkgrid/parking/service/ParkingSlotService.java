package com.parkgrid.parking.service;

import com.parkgrid.parking.dto.SlotDto;
import com.parkgrid.parking.entity.*;
import com.parkgrid.parking.exception.ResourceNotFoundException;
import com.parkgrid.parking.repository.ParkingFacilityRepository;
import com.parkgrid.parking.repository.ParkingSlotRepository;
import com.parkgrid.parking.repository.ParkingZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingSlotService {

    private static final Logger log = LoggerFactory.getLogger(ParkingSlotService.class);

    private final ParkingSlotRepository slotRepository;
    private final ParkingFacilityRepository facilityRepository;
    private final ParkingZoneRepository zoneRepository;

    public ParkingSlotService(ParkingSlotRepository slotRepository, ParkingFacilityRepository facilityRepository, ParkingZoneRepository zoneRepository) {
        this.slotRepository = slotRepository;
        this.facilityRepository = facilityRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<SlotDto> searchSlots(Long facilityId, Long zoneId, SlotStatus status, SlotType slotType) {
        return slotRepository.searchSlots(facilityId, zoneId, status, slotType).stream()
                .map(this::enrichSlotDto)
                .collect(Collectors.toList());
    }

    public SlotDto getSlotById(Long id) {
        ParkingSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found with id: " + id));
        return enrichSlotDto(slot);
    }

    @Transactional
    public SlotDto createSlot(SlotDto dto) {
        if (!facilityRepository.existsById(dto.getFacilityId())) {
            throw new ResourceNotFoundException("Facility not found with id: " + dto.getFacilityId());
        }
        if (!zoneRepository.existsById(dto.getZoneId())) {
            throw new ResourceNotFoundException("Zone not found with id: " + dto.getZoneId());
        }

        ParkingSlot slot = ParkingSlot.builder()
                .slotNumber(dto.getSlotNumber().trim().toUpperCase())
                .facilityId(dto.getFacilityId())
                .zoneId(dto.getZoneId())
                .slotType(dto.getSlotType())
                .status(dto.getStatus() != null ? dto.getStatus() : SlotStatus.AVAILABLE)
                .hourlyRate(dto.getHourlyRate())
                .active(dto.isActive())
                .build();

        return enrichSlotDto(slotRepository.save(slot));
    }

    @Transactional
    public SlotDto updateSlot(Long id, SlotDto dto) {
        ParkingSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found with id: " + id));

        slot.setSlotNumber(dto.getSlotNumber().trim().toUpperCase());
        if (dto.getFacilityId() != null) slot.setFacilityId(dto.getFacilityId());
        if (dto.getZoneId() != null) slot.setZoneId(dto.getZoneId());
        if (dto.getSlotType() != null) slot.setSlotType(dto.getSlotType());
        if (dto.getStatus() != null) slot.setStatus(dto.getStatus());
        if (dto.getHourlyRate() != null) slot.setHourlyRate(dto.getHourlyRate());
        slot.setActive(dto.isActive());

        return enrichSlotDto(slotRepository.save(slot));
    }

    @Transactional
    public SlotDto updateSlotStatus(Long id, SlotStatus status) {
        ParkingSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found with id: " + id));

        slot.setStatus(status);
        return enrichSlotDto(slotRepository.save(slot));
    }

    @Transactional
    public void deleteSlot(Long id) {
        ParkingSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found with id: " + id));
        slot.setActive(false);
        slotRepository.save(slot);
    }

    private SlotDto enrichSlotDto(ParkingSlot slot) {
        String facilityName = facilityRepository.findById(slot.getFacilityId())
                .map(ParkingFacility::getName)
                .orElse("Facility #" + slot.getFacilityId());

        String zoneName = zoneRepository.findById(slot.getZoneId())
                .map(ParkingZone::getName)
                .orElse("Zone #" + slot.getZoneId());

        return SlotDto.builder()
                .id(slot.getId())
                .slotNumber(slot.getSlotNumber())
                .facilityId(slot.getFacilityId())
                .facilityName(facilityName)
                .zoneId(slot.getZoneId())
                .zoneName(zoneName)
                .slotType(slot.getSlotType())
                .status(slot.getStatus())
                .hourlyRate(slot.getHourlyRate())
                .active(slot.isActive())
                .version(slot.getVersion())
                .createdAt(slot.getCreatedAt())
                .updatedAt(slot.getUpdatedAt())
                .build();
    }
}

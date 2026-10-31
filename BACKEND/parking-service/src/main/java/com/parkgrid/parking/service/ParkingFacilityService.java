package com.parkgrid.parking.service;

import com.parkgrid.parking.dto.FacilityAvailabilityDto;
import com.parkgrid.parking.dto.FacilityDto;
import com.parkgrid.parking.dto.ParkingStatisticsDto;
import com.parkgrid.parking.dto.ZoneDto;
import com.parkgrid.parking.entity.ParkingFacility;
import com.parkgrid.parking.entity.ParkingSlot;
import com.parkgrid.parking.entity.SlotStatus;
import com.parkgrid.parking.exception.ResourceNotFoundException;
import com.parkgrid.parking.repository.ParkingFacilityRepository;
import com.parkgrid.parking.repository.ParkingSlotRepository;
import com.parkgrid.parking.repository.ParkingZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingFacilityService {

    private static final Logger log = LoggerFactory.getLogger(ParkingFacilityService.class);

    private final ParkingFacilityRepository facilityRepository;
    private final ParkingZoneRepository zoneRepository;
    private final ParkingSlotRepository slotRepository;

    public ParkingFacilityService(ParkingFacilityRepository facilityRepository, ParkingZoneRepository zoneRepository, ParkingSlotRepository slotRepository) {
        this.facilityRepository = facilityRepository;
        this.zoneRepository = zoneRepository;
        this.slotRepository = slotRepository;
    }

    public List<FacilityDto> getAllFacilities(boolean activeOnly) {
        List<ParkingFacility> facilities = activeOnly ?
                facilityRepository.findByActiveTrue() :
                facilityRepository.findAll();

        return facilities.stream().map(this::enrichFacilityDto).collect(Collectors.toList());
    }

    public FacilityDto getFacilityById(Long id) {
        ParkingFacility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with id: " + id));
        return enrichFacilityDto(facility);
    }

    @Transactional
    public FacilityDto createFacility(FacilityDto dto) {
        ParkingFacility facility = ParkingFacility.builder()
                .name(dto.getName().trim())
                .address(dto.getAddress().trim())
                .description(dto.getDescription())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .active(dto.isActive())
                .build();

        ParkingFacility saved = facilityRepository.save(facility);
        return enrichFacilityDto(saved);
    }

    @Transactional
    public FacilityDto updateFacility(Long id, FacilityDto dto) {
        ParkingFacility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with id: " + id));

        facility.setName(dto.getName().trim());
        facility.setAddress(dto.getAddress().trim());
        facility.setDescription(dto.getDescription());
        facility.setLatitude(dto.getLatitude());
        facility.setLongitude(dto.getLongitude());
        facility.setActive(dto.isActive());

        return enrichFacilityDto(facilityRepository.save(facility));
    }

    @Transactional
    public void deleteFacility(Long id) {
        ParkingFacility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with id: " + id));
        facility.setActive(false);
        facilityRepository.save(facility);
    }

    public List<FacilityAvailabilityDto> getAvailabilityOverview() {
        return facilityRepository.findByActiveTrue().stream().map(facility -> {
            List<ParkingSlot> slots = slotRepository.findByFacilityIdAndActiveTrue(facility.getId());
            int total = slots.size();
            int available = (int) slots.stream().filter(s -> s.getStatus() == SlotStatus.AVAILABLE).count();
            int occupied = (int) slots.stream().filter(s -> s.getStatus() == SlotStatus.OCCUPIED).count();
            double occupancyRate = total > 0 ? ((double) occupied / total) * 100.0 : 0.0;

            List<ZoneDto> zones = zoneRepository.findByFacilityIdAndActiveTrue(facility.getId()).stream().map(zone -> {
                List<ParkingSlot> zoneSlots = slots.stream().filter(s -> s.getZoneId().equals(zone.getId())).collect(Collectors.toList());
                int zTotal = zoneSlots.size();
                int zAvailable = (int) zoneSlots.stream().filter(s -> s.getStatus() == SlotStatus.AVAILABLE).count();
                int zOccupied = (int) zoneSlots.stream().filter(s -> s.getStatus() == SlotStatus.OCCUPIED).count();

                return ZoneDto.builder()
                        .id(zone.getId())
                        .name(zone.getName())
                        .facilityId(facility.getId())
                        .facilityName(facility.getName())
                        .totalSlots(zTotal)
                        .availableSlots(zAvailable)
                        .occupiedSlots(zOccupied)
                        .active(zone.isActive())
                        .build();
            }).collect(Collectors.toList());

            return FacilityAvailabilityDto.builder()
                    .facilityId(facility.getId())
                    .facilityName(facility.getName())
                    .address(facility.getAddress())
                    .totalSlots(total)
                    .availableSlots(available)
                    .occupiedSlots(occupied)
                    .occupancyRate(Math.round(occupancyRate * 10.0) / 10.0)
                    .zones(zones)
                    .build();
        }).collect(Collectors.toList());
    }

    public ParkingStatisticsDto getStatistics() {
        long totalFacilities = facilityRepository.count();
        long totalZones = zoneRepository.count();
        long totalSlots = slotRepository.count();
        long available = slotRepository.countByStatus(SlotStatus.AVAILABLE);
        long occupied = slotRepository.countByStatus(SlotStatus.OCCUPIED);
        long maintenance = slotRepository.countByStatus(SlotStatus.MAINTENANCE) + slotRepository.countByStatus(SlotStatus.INACTIVE);

        double occupancyPct = totalSlots > 0 ? ((double) occupied / totalSlots) * 100.0 : 0.0;

        return ParkingStatisticsDto.builder()
                .totalFacilities(totalFacilities)
                .totalZones(totalZones)
                .totalSlots(totalSlots)
                .availableSlots(available)
                .occupiedSlots(occupied)
                .maintenanceSlots(maintenance)
                .occupancyPercentage(Math.round(occupancyPct * 10.0) / 10.0)
                .build();
    }

    private FacilityDto enrichFacilityDto(ParkingFacility facility) {
        List<ParkingSlot> slots = slotRepository.findByFacilityId(facility.getId());
        int total = slots.size();
        int available = (int) slots.stream().filter(s -> s.getStatus() == SlotStatus.AVAILABLE && s.isActive()).count();
        int occupied = (int) slots.stream().filter(s -> s.getStatus() == SlotStatus.OCCUPIED).count();

        Double minPrice = slots.stream()
                .map(ParkingSlot::getHourlyRate)
                .filter(rate -> rate != null)
                .min(BigDecimal::compareTo)
                .map(BigDecimal::doubleValue)
                .orElse(0.0);

        return FacilityDto.builder()
                .id(facility.getId())
                .name(facility.getName())
                .address(facility.getAddress())
                .description(facility.getDescription())
                .latitude(facility.getLatitude())
                .longitude(facility.getLongitude())
                .active(facility.isActive())
                .totalSlots(total)
                .availableSlots(available)
                .occupiedSlots(occupied)
                .startingPrice(minPrice)
                .createdAt(facility.getCreatedAt())
                .updatedAt(facility.getUpdatedAt())
                .build();
    }
}

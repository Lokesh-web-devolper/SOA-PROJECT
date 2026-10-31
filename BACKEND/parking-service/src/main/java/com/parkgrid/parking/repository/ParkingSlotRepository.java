package com.parkgrid.parking.repository;

import com.parkgrid.parking.entity.ParkingSlot;
import com.parkgrid.parking.entity.SlotStatus;
import com.parkgrid.parking.entity.SlotType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findByFacilityId(Long facilityId);
    List<ParkingSlot> findByZoneId(Long zoneId);
    List<ParkingSlot> findByFacilityIdAndStatus(Long facilityId, SlotStatus status);
    List<ParkingSlot> findByZoneIdAndStatus(Long zoneId, SlotStatus status);
    List<ParkingSlot> findByFacilityIdAndActiveTrue(Long facilityId);

    long countByStatus(SlotStatus status);
    long countByFacilityIdAndStatus(Long facilityId, SlotStatus status);
    long countByZoneIdAndStatus(Long zoneId, SlotStatus status);

    @Query("SELECT s FROM ParkingSlot s WHERE " +
           "(:facilityId IS NULL OR s.facilityId = :facilityId) AND " +
           "(:zoneId IS NULL OR s.zoneId = :zoneId) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:slotType IS NULL OR s.slotType = :slotType) AND " +
           "s.active = true ORDER BY s.slotNumber ASC")
    List<ParkingSlot> searchSlots(@Param("facilityId") Long facilityId,
                                  @Param("zoneId") Long zoneId,
                                  @Param("status") SlotStatus status,
                                  @Param("slotType") SlotType slotType);
}

package com.parkgrid.parking.repository;

import com.parkgrid.parking.entity.ParkingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingZoneRepository extends JpaRepository<ParkingZone, Long> {
    List<ParkingZone> findByFacilityId(Long facilityId);
    List<ParkingZone> findByFacilityIdAndActiveTrue(Long facilityId);
    List<ParkingZone> findByActiveTrue();
}

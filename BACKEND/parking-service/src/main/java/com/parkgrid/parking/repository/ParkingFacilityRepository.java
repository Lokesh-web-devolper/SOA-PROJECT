package com.parkgrid.parking.repository;

import com.parkgrid.parking.entity.ParkingFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingFacilityRepository extends JpaRepository<ParkingFacility, Long> {
    List<ParkingFacility> findByActiveTrue();
}

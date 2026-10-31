package com.parkgrid.billing.repository;

import com.parkgrid.billing.entity.PricingConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingConfigurationRepository extends JpaRepository<PricingConfiguration, Long> {
    Optional<PricingConfiguration> findByFacilityId(Long facilityId);
}

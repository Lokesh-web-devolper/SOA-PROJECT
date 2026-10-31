package com.parkgrid.billing.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pricing_configurations")
public class PricingConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long facilityId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultHourlyRate;

    @Column(nullable = false)
    private Integer gracePeriodMinutes = 15;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal additionalCharges = BigDecimal.ZERO;

    public PricingConfiguration() {}

    public PricingConfiguration(Long id, Long facilityId, BigDecimal defaultHourlyRate, Integer gracePeriodMinutes, BigDecimal additionalCharges) {
        this.id = id;
        this.facilityId = facilityId;
        this.defaultHourlyRate = defaultHourlyRate;
        this.gracePeriodMinutes = gracePeriodMinutes != null ? gracePeriodMinutes : 15;
        this.additionalCharges = additionalCharges != null ? additionalCharges : BigDecimal.ZERO;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long facilityId;
        private BigDecimal defaultHourlyRate;
        private Integer gracePeriodMinutes = 15;
        private BigDecimal additionalCharges = BigDecimal.ZERO;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder facilityId(Long facilityId) { this.facilityId = facilityId; return this; }
        public Builder defaultHourlyRate(BigDecimal defaultHourlyRate) { this.defaultHourlyRate = defaultHourlyRate; return this; }
        public Builder gracePeriodMinutes(Integer gracePeriodMinutes) { this.gracePeriodMinutes = gracePeriodMinutes; return this; }
        public Builder additionalCharges(BigDecimal additionalCharges) { this.additionalCharges = additionalCharges; return this; }

        public PricingConfiguration build() {
            return new PricingConfiguration(id, facilityId, defaultHourlyRate, gracePeriodMinutes, additionalCharges);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public BigDecimal getDefaultHourlyRate() { return defaultHourlyRate; }
    public void setDefaultHourlyRate(BigDecimal defaultHourlyRate) { this.defaultHourlyRate = defaultHourlyRate; }

    public Integer getGracePeriodMinutes() { return gracePeriodMinutes; }
    public void setGracePeriodMinutes(Integer gracePeriodMinutes) { this.gracePeriodMinutes = gracePeriodMinutes; }

    public BigDecimal getAdditionalCharges() { return additionalCharges; }
    public void setAdditionalCharges(BigDecimal additionalCharges) { this.additionalCharges = additionalCharges; }
}

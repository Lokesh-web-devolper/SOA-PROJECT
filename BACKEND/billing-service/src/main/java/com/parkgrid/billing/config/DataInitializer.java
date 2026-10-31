package com.parkgrid.billing.config;

import com.parkgrid.billing.entity.Invoice;
import com.parkgrid.billing.entity.PaymentStatus;
import com.parkgrid.billing.entity.PricingConfiguration;
import com.parkgrid.billing.repository.InvoiceRepository;
import com.parkgrid.billing.repository.PricingConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final InvoiceRepository invoiceRepository;
    private final PricingConfigurationRepository pricingRepository;

    public DataInitializer(InvoiceRepository invoiceRepository, PricingConfigurationRepository pricingRepository) {
        this.invoiceRepository = invoiceRepository;
        this.pricingRepository = pricingRepository;
    }

    @Override
    public void run(String... args) {
        if (pricingRepository.count() == 0) {
            pricingRepository.save(PricingConfiguration.builder()
                    .facilityId(1L)
                    .defaultHourlyRate(new BigDecimal("15.00"))
                    .gracePeriodMinutes(15)
                    .additionalCharges(BigDecimal.ZERO)
                    .build());

            pricingRepository.save(PricingConfiguration.builder()
                    .facilityId(2L)
                    .defaultHourlyRate(new BigDecimal("12.50"))
                    .gracePeriodMinutes(15)
                    .additionalCharges(BigDecimal.ZERO)
                    .build());

            pricingRepository.save(PricingConfiguration.builder()
                    .facilityId(3L)
                    .defaultHourlyRate(new BigDecimal("8.00"))
                    .gracePeriodMinutes(20)
                    .additionalCharges(BigDecimal.ZERO)
                    .build());

            log.info("Initialized default facility pricing configurations.");
        }

        if (invoiceRepository.count() == 0) {
            invoiceRepository.save(Invoice.builder()
                    .invoiceNumber("INV-20261028-1001A")
                    .bookingId(1L)
                    .bookingReference("PG-20261028-1001A")
                    .userId(2L)
                    .userEmail("customer@parkgrid.com")
                    .facilityId(1L)
                    .entryTime(LocalDateTime.now().minusDays(2).withHour(9).withMinute(5))
                    .exitTime(LocalDateTime.now().minusDays(2).withHour(11).withMinute(50))
                    .parkingDurationMinutes(165L)
                    .billableHours(3.0)
                    .hourlyRate(new BigDecimal("15.00"))
                    .totalAmount(new BigDecimal("45.00"))
                    .paymentStatus(PaymentStatus.PAID)
                    .createdAt(LocalDateTime.now().minusDays(2).withHour(11).withMinute(55))
                    .build());

            log.info("Initialized sample paid invoice.");
        }
    }
}

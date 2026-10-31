package com.parkgrid.billing.repository;

import com.parkgrid.billing.entity.Invoice;
import com.parkgrid.billing.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByBookingId(Long bookingId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Invoice> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.paymentStatus = 'PAID'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND i.createdAt >= :since")
    BigDecimal calculateRevenueSince(LocalDateTime since);
}

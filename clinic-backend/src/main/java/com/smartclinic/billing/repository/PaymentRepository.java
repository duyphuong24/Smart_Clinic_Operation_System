package com.smartclinic.billing.repository;

import com.smartclinic.billing.entity.Payment;
import com.smartclinic.billing.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceId(Long invoiceId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByInvoiceIdOrderByPaidAtDesc(Long invoiceId);

    List<Payment> findByPaidAtBetweenAndStatus(LocalDateTime start, LocalDateTime end, PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paidAt >= :start AND p.paidAt < :end AND p.status = :status")
    BigDecimal sumAmountByPaidAtBetweenAndStatus(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") PaymentStatus status
    );
}

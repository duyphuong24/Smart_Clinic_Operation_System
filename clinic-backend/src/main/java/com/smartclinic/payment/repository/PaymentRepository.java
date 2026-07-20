package com.smartclinic.payment.repository;

import com.smartclinic.payment.entity.Payment;
import com.smartclinic.payment.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceId(Long invoiceId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where p.paidAt >= :start
              and p.paidAt < :end
              and p.status = :status
            """)
    BigDecimal sumAmountByPaidAtBetweenAndStatus(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") PaymentStatus status
    );
}
package com.smartclinic.payment.repository;

import com.smartclinic.payment.entity.Payment;
import com.smartclinic.payment.entity.PaymentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceId(Long invoiceId);

    List<Payment> findByStatus(PaymentStatus status);
}
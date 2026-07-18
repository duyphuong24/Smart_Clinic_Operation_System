package com.smartclinic.invoice.repository;

import com.smartclinic.invoice.entity.Invoice;
import com.smartclinic.invoice.entity.InvoiceStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByVisitId(Long visitId);

    boolean existsByInvoiceNumber(String invoiceNumber);

    long countByIssuedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Invoice> findByStatus(InvoiceStatus status);
}
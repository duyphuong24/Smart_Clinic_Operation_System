package com.smartclinic.billing.repository;

import com.smartclinic.billing.entity.Invoice;
import com.smartclinic.billing.entity.InvoiceStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByVisitId(Long visitId);

    Optional<Invoice> findByEncounterId(Long encounterId);

    boolean existsByVisitId(Long visitId);

    boolean existsByInvoiceNumber(String invoiceNumber);

    long countByIssuedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Invoice> findByStatusOrderByIssuedAtDesc(InvoiceStatus status);

    List<Invoice> findByStatus(InvoiceStatus status);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceNumber LIKE :prefix%")
    long countByInvoiceNumberPrefix(String prefix);
}

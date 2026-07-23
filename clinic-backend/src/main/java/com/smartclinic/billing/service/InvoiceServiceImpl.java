package com.smartclinic.billing.service;

import com.smartclinic.billing.dto.InvoiceCancelRequest;
import com.smartclinic.billing.dto.InvoiceCreateRequest;
import com.smartclinic.billing.dto.InvoiceResponse;
import com.smartclinic.billing.entity.Invoice;
import com.smartclinic.billing.entity.InvoiceItem;
import com.smartclinic.billing.entity.InvoiceItemType;
import com.smartclinic.billing.entity.InvoiceStatus;
import com.smartclinic.billing.mapper.InvoiceMapper;
import com.smartclinic.billing.repository.InvoiceRepository;
import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.entity.EncounterStatus;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrder;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrderStatus;
import com.smartclinic.encounter.serviceorder.repository.EncounterServiceOrderRepository;
import com.smartclinic.visit.entity.Visit;
import com.smartclinic.visit.entity.VisitStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class InvoiceServiceImpl implements InvoiceService {

    private static final DateTimeFormatter INVOICE_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    private final InvoiceRepository invoiceRepository;
    private final EncounterRepository encounterRepository;
    private final EncounterServiceOrderRepository encounterServiceOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAll(InvoiceStatus status) {
        List<Invoice> invoices = status == null ? invoiceRepository.findAll() : invoiceRepository.findByStatus(status);
        return invoices.stream().map(InvoiceMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encounter> findPendingBillings() {
        return encounterRepository.findAll().stream()
                .filter(e -> e.getStatus() == EncounterStatus.COMPLETED)
                .filter(e -> e.getVisit() != null && !invoiceRepository.existsByVisitId(e.getVisit().getId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getById(Long id) {
        return InvoiceMapper.toResponse(findInvoice(id));
    }

    @Override
    public InvoiceResponse generate(InvoiceCreateRequest request) {
        Encounter encounter = encounterRepository.findById(request.getEncounterId())
                .orElseThrow(() -> new ResourceNotFoundException("Encounter not found"));
        if (encounter.getStatus() != EncounterStatus.COMPLETED) {
            throw new BadRequestException("Invoice can only be generated after encounter is completed");
        }
        Visit visit = encounter.getVisit();
        if (visit.getStatus() != VisitStatus.COMPLETED) {
            throw new BadRequestException("Invoice can only be generated for completed visit");
        }
        if (invoiceRepository.existsByVisitId(visit.getId())) {
            throw new DuplicateResourceException("Visit already has an invoice");
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(nextInvoiceNumber());
        invoice.setEncounter(encounter);
        invoice.setVisit(visit);
        invoice.setPatient(visit.getPatient());
        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setIssuedAt(LocalDateTime.now());
        invoice.setTotalAmount(BigDecimal.ZERO);
        invoice.addItem(consultationItem(encounter));

        encounterServiceOrderRepository
                .findByEncounterIdAndStatusNot(encounter.getId(), EncounterServiceOrderStatus.CANCELLED)
                .forEach(order -> invoice.addItem(serviceItem(order)));

        return InvoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponse cancel(Long id, InvoiceCancelRequest request) {
        Invoice invoice = findInvoice(id);
        if (invoice.getStatus() != InvoiceStatus.ISSUED) {
            throw new BadRequestException("Only ISSUED invoice can be cancelled");
        }
        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice.setCancelledAt(LocalDateTime.now());
        invoice.setCancelReason(trimToNull(request.getReason()));
        return InvoiceMapper.toResponse(invoiceRepository.save(invoice));
    }

    private Invoice findInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }

    private InvoiceItem consultationItem(Encounter encounter) {
        BigDecimal fee = encounter.getDoctor().getConsultationFee();
        InvoiceItem item = new InvoiceItem();
        item.setItemType(InvoiceItemType.CONSULTATION);
        item.setReferenceId(encounter.getDoctor().getId());
        item.setDescription("Doctor consultation fee");
        item.setQuantity(1);
        item.setUnitPrice(fee);
        item.setLineTotal(fee);
        return item;
    }

    private InvoiceItem serviceItem(EncounterServiceOrder order) {
        InvoiceItem item = new InvoiceItem();
        item.setItemType(InvoiceItemType.SERVICE);
        item.setReferenceId(order.getId());
        item.setDescription(order.getServiceCatalog().getName());
        item.setQuantity(order.getQuantity());
        item.setUnitPrice(order.getUnitPrice());
        item.setLineTotal(order.getTotalAmount());
        return item;
    }

    private String nextInvoiceNumber() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        long sequence = invoiceRepository.countByIssuedAtBetween(start, end) + 1;
        String prefix = "INV-" + today.format(INVOICE_DATE_FORMAT) + "-";
        String invoiceNumber = prefix + String.format("%06d", sequence);
        while (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            sequence++;
            invoiceNumber = prefix + String.format("%06d", sequence);
        }
        return invoiceNumber;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}

package com.smartclinic.billing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartclinic.billing.dto.InvoiceCancelRequest;
import com.smartclinic.billing.dto.InvoiceCreateRequest;
import com.smartclinic.billing.dto.InvoiceResponse;
import com.smartclinic.billing.entity.Invoice;
import com.smartclinic.billing.entity.InvoiceStatus;
import com.smartclinic.billing.repository.InvoiceRepository;
import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.entity.EncounterStatus;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrder;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrderStatus;
import com.smartclinic.encounter.serviceorder.repository.EncounterServiceOrderRepository;
import com.smartclinic.patient.entity.Patient;
import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.entity.ServiceType;
import com.smartclinic.visit.entity.Visit;
import com.smartclinic.visit.entity.VisitStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private EncounterServiceOrderRepository encounterServiceOrderRepository;

    @InjectMocks
    private InvoiceServiceImpl service;

    @Test
    void generateShouldRejectOpenEncounter() {
        Encounter encounter = encounter(EncounterStatus.OPEN, VisitStatus.IN_CONSULTATION);
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));

        assertThatThrownBy(() -> service.generate(request(1L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invoice can only be generated after encounter is completed");
    }

    @Test
    void generateShouldRejectIncompleteVisit() {
        Encounter encounter = encounter(EncounterStatus.COMPLETED, VisitStatus.IN_CONSULTATION);
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));

        assertThatThrownBy(() -> service.generate(request(1L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invoice can only be generated for completed visit");
    }

    @Test
    void generateShouldRejectDuplicateVisitInvoice() {
        Encounter encounter = encounter(EncounterStatus.COMPLETED, VisitStatus.COMPLETED);
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));
        when(invoiceRepository.existsByVisitId(10L)).thenReturn(true);

        assertThatThrownBy(() -> service.generate(request(1L)))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Visit already has an invoice");
    }

    @Test
    void generateShouldIncludeConsultationAndNonCancelledServiceOrders() {
        Encounter encounter = encounter(EncounterStatus.COMPLETED, VisitStatus.COMPLETED);
        EncounterServiceOrder completedOrder = order(20L, EncounterServiceOrderStatus.COMPLETED, BigDecimal.valueOf(250000), 2);
        EncounterServiceOrder orderedOrder = order(21L, EncounterServiceOrderStatus.ORDERED, BigDecimal.valueOf(100000), 1);
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));
        when(invoiceRepository.existsByVisitId(10L)).thenReturn(false);
        when(invoiceRepository.countByIssuedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(0L);
        when(invoiceRepository.existsByInvoiceNumber(any(String.class))).thenReturn(false);
        when(encounterServiceOrderRepository.findByEncounterIdAndStatusNot(1L, EncounterServiceOrderStatus.CANCELLED))
                .thenReturn(List.of(completedOrder, orderedOrder));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(99L);
            return invoice;
        });

        InvoiceResponse response = service.generate(request(1L));

        assertThat(response.getStatus()).isEqualTo(InvoiceStatus.ISSUED);
        assertThat(response.getTotalAmount()).isEqualByComparingTo("750000");
        assertThat(response.getItems()).hasSize(3);
        assertThat(response.getInvoiceNumber()).startsWith("INV-");
    }

    @Test
    void cancelShouldRejectPaidInvoice() {
        Invoice invoice = invoice(InvoiceStatus.PAID);
        when(invoiceRepository.findById(99L)).thenReturn(Optional.of(invoice));

        assertThatThrownBy(() -> service.cancel(99L, cancelRequest(" duplicate ")))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only ISSUED invoice can be cancelled");
    }

    @Test
    void cancelShouldMarkInvoiceCancelled() {
        Invoice invoice = invoice(InvoiceStatus.ISSUED);
        when(invoiceRepository.findById(99L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);

        InvoiceResponse response = service.cancel(99L, cancelRequest(" duplicate "));

        assertThat(response.getStatus()).isEqualTo(InvoiceStatus.CANCELLED);
        assertThat(response.getCancelReason()).isEqualTo("duplicate");
        verify(invoiceRepository).save(invoice);
    }

    private InvoiceCreateRequest request(Long encounterId) {
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setEncounterId(encounterId);
        return request;
    }

    private InvoiceCancelRequest cancelRequest(String reason) {
        InvoiceCancelRequest request = new InvoiceCancelRequest();
        request.setReason(reason);
        return request;
    }

    private Encounter encounter(EncounterStatus encounterStatus, VisitStatus visitStatus) {
        Patient patient = new Patient();
        patient.setId(5L);
        patient.setPatientCode("PT-000005");
        patient.setFullName("Nguyen Van A");

        Doctor doctor = new Doctor();
        doctor.setId(7L);
        doctor.setConsultationFee(BigDecimal.valueOf(150000));

        Visit visit = new Visit();
        visit.setId(10L);
        visit.setVisitCode("VIS-000010");
        visit.setPatient(patient);
        visit.setDoctor(doctor);
        visit.setStatus(visitStatus);

        Encounter encounter = new Encounter();
        encounter.setId(1L);
        encounter.setVisit(visit);
        encounter.setDoctor(doctor);
        encounter.setStatus(encounterStatus);
        return encounter;
    }

    private EncounterServiceOrder order(Long id, EncounterServiceOrderStatus status, BigDecimal unitPrice, int quantity) {
        ServiceCatalog catalog = new ServiceCatalog();
        catalog.setId(30L);
        catalog.setServiceCode("LAB-001");
        catalog.setName("Blood test");
        catalog.setType(ServiceType.LAB_TEST);
        catalog.setPrice(unitPrice);
        catalog.setActive(true);

        EncounterServiceOrder order = new EncounterServiceOrder();
        order.setId(id);
        order.setServiceCatalog(catalog);
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        order.setStatus(status);
        return order;
    }

    private Invoice invoice(InvoiceStatus status) {
        Encounter encounter = encounter(EncounterStatus.COMPLETED, VisitStatus.COMPLETED);
        Invoice invoice = new Invoice();
        invoice.setId(99L);
        invoice.setInvoiceNumber("INV-20260718-000001");
        invoice.setVisit(encounter.getVisit());
        invoice.setEncounter(encounter);
        invoice.setPatient(encounter.getVisit().getPatient());
        invoice.setTotalAmount(BigDecimal.valueOf(150000));
        invoice.setStatus(status);
        invoice.setIssuedAt(LocalDateTime.now());
        return invoice;
    }
}

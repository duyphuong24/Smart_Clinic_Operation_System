package com.smartclinic.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.smartclinic.appointment.dto.AppointmentRequest;
import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.appointment.entity.AppointmentStatus;
import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.appointment.service.AppointmentServiceImpl;
import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.encounter.dto.EncounterCreateRequest;
import com.smartclinic.encounter.dto.EncounterUpdateRequest;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.entity.EncounterStatus;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.encounter.service.EncounterWorkflowServiceImpl;
import com.smartclinic.patient.entity.Patient;
import com.smartclinic.patient.repository.PatientRepository;
import com.smartclinic.billing.dto.InvoiceCreateRequest;
import com.smartclinic.billing.dto.PaymentRequest;
import com.smartclinic.billing.entity.Invoice;
import com.smartclinic.billing.entity.InvoiceStatus;
import com.smartclinic.billing.entity.PaymentMethod;
import com.smartclinic.billing.entity.PaymentStatus;
import com.smartclinic.billing.repository.InvoiceRepository;
import com.smartclinic.billing.repository.PaymentRepository;
import com.smartclinic.billing.service.InvoiceServiceImpl;
import com.smartclinic.billing.service.PaymentServiceImpl;
import com.smartclinic.queue.dto.AppointmentCheckInRequest;
import com.smartclinic.queue.entity.QueuePriority;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.queue.service.QueueItemServiceImpl;
import com.smartclinic.schedule.repository.DoctorAvailabilityRepository;
import com.smartclinic.visit.entity.Visit;
import com.smartclinic.visit.entity.VisitStatus;
import com.smartclinic.visit.repository.VisitRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class WorkflowBusinessRulesTest {

    // 1. Appointment availability and doctor slot rules
    @Test
    void appointmentShouldValidateDoctorAvailabilityAndOverlaps() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        PatientRepository patientRepository = mock(PatientRepository.class);
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        DoctorAvailabilityRepository availabilityRepository = mock(DoctorAvailabilityRepository.class);

        AppointmentServiceImpl appointmentService = new AppointmentServiceImpl(
                appointmentRepository, patientRepository, doctorRepository, mock(com.smartclinic.masterdata.repository.RoomRepository.class), availabilityRepository, mock(com.smartclinic.notification.service.NotificationService.class)
        );

        Patient patient = new Patient();
        com.smartclinic.masterdata.entity.Room room = new com.smartclinic.masterdata.entity.Room();
        room.setId(1L);
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setDefaultRoom(room);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        // Outside availability
        when(availabilityRepository.existsActiveAvailabilityCovering(eq(1L), any(Integer.class), any(), any()))
                .thenReturn(false);

        AppointmentRequest req = new AppointmentRequest();
        req.setPatientId(1L);
        req.setDoctorId(1L);
        req.setScheduledStart(LocalDateTime.now());
        req.setScheduledEnd(LocalDateTime.now().plusMinutes(30));

        assertThatThrownBy(() -> appointmentService.create(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("availability");

        // Overlapping active appointment
        when(availabilityRepository.existsActiveAvailabilityCovering(eq(1L), any(Integer.class), any(), any()))
                .thenReturn(true);
        when(appointmentRepository.existsActiveOverlap(eq(1L), any(), any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> appointmentService.create(req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("active appointment");
    }

    // 2. Queue rules: cancelled appointment cannot check in
    @Test
    void cancelledAppointmentCannotCheckIn() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        QueueItemRepository queueItemRepository = mock(QueueItemRepository.class);
        QueueItemServiceImpl queueService = new QueueItemServiceImpl(
                queueItemRepository, appointmentRepository, mock(PatientRepository.class),
                mock(DoctorRepository.class), mock(com.smartclinic.masterdata.repository.RoomRepository.class)
        );

        Appointment cancelledAppointment = Appointment.builder().status(AppointmentStatus.CANCELLED).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(cancelledAppointment));

        AppointmentCheckInRequest req = new AppointmentCheckInRequest();
        req.setAppointmentId(1L);
        req.setPriority(QueuePriority.NORMAL);

        assertThatThrownBy(() -> queueService.checkIn(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only BOOKED appointment can be checked in");
    }

    // 3. Consultation rules: completed encounter cannot be edited
    @Test
    void completedEncounterCannotBeEdited() {
        EncounterRepository encounterRepository = mock(EncounterRepository.class);
        EncounterWorkflowServiceImpl encounterService = new EncounterWorkflowServiceImpl(
                encounterRepository, mock(VisitRepository.class), mock(AppointmentRepository.class), mock(QueueItemRepository.class)
        );

        Encounter completedEncounter = new Encounter();
        completedEncounter.setStatus(EncounterStatus.COMPLETED);
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(completedEncounter));

        EncounterUpdateRequest req = new EncounterUpdateRequest();
        req.setDiagnosis("New Diagnosis");

        assertThatThrownBy(() -> encounterService.update(1L, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Completed encounter is read-only");
    }

    // 4. Invoicing rules: invoice only after completed encounter
    @Test
    void invoiceOnlyAfterCompletedEncounter() {
        EncounterRepository encounterRepository = mock(EncounterRepository.class);
        InvoiceRepository invoiceRepository = mock(InvoiceRepository.class);
        InvoiceServiceImpl invoiceService = new InvoiceServiceImpl(
                invoiceRepository, encounterRepository, mock(com.smartclinic.encounter.serviceorder.repository.EncounterServiceOrderRepository.class)
        );

        Encounter openEncounter = new Encounter();
        openEncounter.setStatus(EncounterStatus.OPEN);
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(openEncounter));

        InvoiceCreateRequest req = new InvoiceCreateRequest();
        req.setEncounterId(1L);

        assertThatThrownBy(() -> invoiceService.generate(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("after encounter is completed");
    }

    // 5. Payment rules: payment amount must equal invoice total
    @Test
    void paymentAmountMustEqualInvoiceTotal() {
        InvoiceRepository invoiceRepository = mock(InvoiceRepository.class);
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        PaymentServiceImpl paymentService = new PaymentServiceImpl(invoiceRepository, paymentRepository);

        Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setTotalAmount(BigDecimal.valueOf(150000));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        PaymentRequest req = new PaymentRequest();
        req.setAmount(BigDecimal.valueOf(100000)); // Mismatch
        req.setMethod(PaymentMethod.CASH);
        req.setStatus(PaymentStatus.SUCCESS);

        assertThatThrownBy(() -> paymentService.record(1L, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Payment amount must equal invoice total");
    }
}

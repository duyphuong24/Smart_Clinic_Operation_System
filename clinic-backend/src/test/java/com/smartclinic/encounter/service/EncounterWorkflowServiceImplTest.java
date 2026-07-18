package com.smartclinic.encounter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.appointment.entity.AppointmentSource;
import com.smartclinic.appointment.entity.AppointmentStatus;
import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.encounter.dto.EncounterCreateRequest;
import com.smartclinic.encounter.dto.EncounterResponse;
import com.smartclinic.encounter.dto.EncounterUpdateRequest;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.entity.EncounterStatus;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.entity.Specialty;
import com.smartclinic.patient.entity.Gender;
import com.smartclinic.patient.entity.Patient;
import com.smartclinic.patient.entity.PatientStatus;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.entity.QueuePriority;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.staff.entity.Staff;
import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.entity.StaffType;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.visit.entity.Visit;
import com.smartclinic.visit.entity.VisitStatus;
import com.smartclinic.visit.repository.VisitRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EncounterWorkflowServiceImplTest {

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private QueueItemRepository queueItemRepository;

    @InjectMocks
    private EncounterWorkflowServiceImpl service;

    @Test
    void createShouldRejectVisitThatIsNotInConsultation() {
        Visit visit = visit(VisitStatus.WAITING);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));

        assertThatThrownBy(() -> service.create(createRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only IN_CONSULTATION visit can open encounter");
    }

    @Test
    void createShouldRejectDuplicateEncounterForVisit() {
        Visit visit = visit(VisitStatus.IN_CONSULTATION);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(encounterRepository.existsByVisitId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.create(createRequest()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Visit already has an encounter");
    }

    @Test
    void updateShouldRejectCompletedEncounter() {
        Encounter encounter = encounter(EncounterStatus.COMPLETED, visit(VisitStatus.COMPLETED));
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));

        assertThatThrownBy(() -> service.update(1L, updateRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Completed encounter is read-only");
    }

    @Test
    void completeShouldMarkEncounterVisitAppointmentAndQueueDone() {
        Appointment appointment = appointment(AppointmentStatus.IN_CONSULTATION);
        QueueItem item = queueItem(QueueStatus.IN_SERVICE, appointment);
        Visit visit = visit(VisitStatus.IN_CONSULTATION);
        visit.setAppointment(appointment);
        visit.setQueueItem(item);
        Encounter encounter = encounter(EncounterStatus.OPEN, visit);
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));
        when(encounterRepository.save(any(Encounter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EncounterResponse response = service.complete(1L);

        assertThat(response.getStatus()).isEqualTo(EncounterStatus.COMPLETED);
        assertThat(response.getCompletedAt()).isNotNull();
        assertThat(visit.getStatus()).isEqualTo(VisitStatus.COMPLETED);
        assertThat(visit.getEndedAt()).isNotNull();
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(item.getStatus()).isEqualTo(QueueStatus.DONE);
    }

    private EncounterCreateRequest createRequest() {
        EncounterCreateRequest request = new EncounterCreateRequest();
        request.setVisitId(1L);
        request.setChiefComplaint("Headache");
        request.setDiagnosis("Migraine");
        request.setClinicalNote("Rest and hydrate");
        return request;
    }

    private EncounterUpdateRequest updateRequest() {
        EncounterUpdateRequest request = new EncounterUpdateRequest();
        request.setChiefComplaint("Updated complaint");
        request.setDiagnosis("Updated diagnosis");
        request.setClinicalNote("Updated note");
        return request;
    }

    private Encounter encounter(EncounterStatus status, Visit visit) {
        Encounter encounter = new Encounter();
        encounter.setId(1L);
        encounter.setVisit(visit);
        encounter.setDoctor(visit.getDoctor());
        encounter.setChiefComplaint("Headache");
        encounter.setDiagnosis("Migraine");
        encounter.setClinicalNote("Rest");
        encounter.setStatus(status);
        encounter.setStartedAt(LocalDateTime.of(2030, 1, 7, 8, 15));
        encounter.setCompletedAt(status == EncounterStatus.COMPLETED ? LocalDateTime.of(2030, 1, 7, 8, 45) : null);
        return encounter;
    }

    private Visit visit(VisitStatus status) {
        Visit visit = new Visit();
        visit.setId(1L);
        visit.setVisitCode("VIS-000001");
        visit.setPatient(patient());
        visit.setDoctor(doctor());
        visit.setStatus(status);
        visit.setStartedAt(LocalDateTime.of(2030, 1, 7, 8, 10));
        return visit;
    }

    private QueueItem queueItem(QueueStatus status, Appointment appointment) {
        QueueItem item = QueueItem.builder()
                .queueDate(LocalDate.of(2030, 1, 7))
                .queueNumber(1)
                .patient(patient())
                .appointment(appointment)
                .doctor(doctor())
                .room(room())
                .priority(QueuePriority.NORMAL)
                .status(status)
                .build();
        item.setId(1L);
        return item;
    }

    private Appointment appointment(AppointmentStatus status) {
        Appointment appointment = Appointment.builder()
                .appointmentCode("APT-000001")
                .patient(patient())
                .doctor(doctor())
                .room(room())
                .scheduledStart(LocalDateTime.of(2030, 1, 7, 8, 0))
                .scheduledEnd(LocalDateTime.of(2030, 1, 7, 8, 30))
                .reason("General consultation")
                .source(AppointmentSource.PHONE)
                .status(status)
                .build();
        appointment.setId(1L);
        return appointment;
    }

    private Patient patient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setPatientCode("PAT-000001");
        patient.setFullName("Nguyen Van A");
        patient.setGender(Gender.MALE);
        patient.setStatus(PatientStatus.ACTIVE);
        return patient;
    }

    private Doctor doctor() {
        User user = User.builder()
                .id(1L)
                .userName("doctor")
                .fullName("Demo Doctor")
                .passwordHash("hash")
                .status(UserStatus.ACTIVE)
                .build();
        Staff staff = Staff.builder()
                .user(user)
                .employeeCode("DOC-001")
                .staffType(StaffType.DOCTOR)
                .status(StaffStatus.ACTIVE)
                .build();
        staff.setId(1L);
        Specialty specialty = Specialty.builder()
                .id(1L)
                .name("General")
                .active(true)
                .build();
        Doctor doctor = Doctor.builder()
                .staff(staff)
                .specialty(specialty)
                .defaultRoom(room())
                .licenseNo("LIC-001")
                .active(true)
                .build();
        doctor.setId(1L);
        return doctor;
    }

    private Room room() {
        return Room.builder()
                .id(1L)
                .roomCode("R-101")
                .name("Room 101")
                .active(true)
                .build();
    }
}
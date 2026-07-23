package com.smartclinic.visit.service;

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
import com.smartclinic.visit.dto.VisitCreateRequest;
import com.smartclinic.visit.dto.VisitResponse;
import com.smartclinic.visit.entity.Visit;
import com.smartclinic.visit.entity.VisitStatus;
import com.smartclinic.visit.repository.VisitRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import com.smartclinic.doctor.repository.DoctorRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VisitServiceImplTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private QueueItemRepository queueItemRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private VisitServiceImpl service;

    @Test
    void createShouldRejectQueueItemThatIsNotInService() {
        QueueItem item = queueItem(QueueStatus.WAITING, appointment(AppointmentStatus.CHECKED_IN));
        when(queueItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> service.create(queueVisitRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only IN_SERVICE queue item can create visit");
    }

    @Test
    void createShouldRejectDuplicateQueueVisit() {
        QueueItem item = queueItem(QueueStatus.IN_SERVICE, appointment(AppointmentStatus.IN_CONSULTATION));
        when(queueItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(visitRepository.existsByQueueItemId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.create(queueVisitRequest()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Queue item already has a visit");
    }

    @Test
    void createFromAppointmentShouldRejectNonCheckedInAppointment() {
        Appointment appointment = appointment(AppointmentStatus.BOOKED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.create(appointmentVisitRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only checked-in appointment can create visit");
    }

    @Test
    void createFromAppointmentShouldMarkAppointmentInConsultation() {
        Appointment appointment = appointment(AppointmentStatus.CHECKED_IN);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(visitRepository.existsByAppointmentId(1L)).thenReturn(false);
        when(visitRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(visitRepository.existsByVisitCode("VIS-000001")).thenReturn(false);
        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> {
            Visit visit = invocation.getArgument(0);
            visit.setId(1L);
            return visit;
        });

        VisitResponse response = service.create(appointmentVisitRequest());

        assertThat(response.getVisitCode()).isEqualTo("VIS-000001");
        assertThat(response.getStatus()).isEqualTo(VisitStatus.IN_CONSULTATION);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.IN_CONSULTATION);
    }

    private VisitCreateRequest queueVisitRequest() {
        VisitCreateRequest request = new VisitCreateRequest();
        request.setQueueItemId(1L);
        return request;
    }

    private VisitCreateRequest appointmentVisitRequest() {
        VisitCreateRequest request = new VisitCreateRequest();
        request.setAppointmentId(1L);
        return request;
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
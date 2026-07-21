package com.smartclinic.queue.service;

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
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.entity.Specialty;
import com.smartclinic.masterdata.repository.RoomRepository;
import com.smartclinic.patient.entity.Gender;
import com.smartclinic.patient.entity.Patient;
import com.smartclinic.patient.entity.PatientStatus;
import com.smartclinic.patient.repository.PatientRepository;
import com.smartclinic.queue.dto.AppointmentCheckInRequest;
import com.smartclinic.queue.dto.QueueItemResponse;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.entity.QueuePriority;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.staff.entity.Staff;
import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.entity.StaffType;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QueueItemServiceImplTest {

    @Mock
    private QueueItemRepository queueItemRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private QueueItemServiceImpl service;

    @Test
    void checkInShouldRejectNonBookedAppointment() {
        Appointment appointment = appointment(AppointmentStatus.CHECKED_IN);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.checkIn(checkInRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only BOOKED appointment can be checked in");
    }

    @Test
    void checkInShouldRejectDuplicateQueueItem() {
        Appointment appointment = appointment(AppointmentStatus.BOOKED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(queueItemRepository.existsByAppointmentId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.checkIn(checkInRequest()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Appointment already has a queue item");
    }

    @Test
    void checkInShouldCreateWaitingQueueAndMarkAppointmentCheckedIn() {
        Appointment appointment = appointment(AppointmentStatus.BOOKED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(queueItemRepository.existsByAppointmentId(1L)).thenReturn(false);
        when(queueItemRepository.findTopByQueueDateOrderByQueueNumberDesc(any())).thenReturn(Optional.empty());
        when(queueItemRepository.existsByQueueDateAndQueueNumber(any(), any())).thenReturn(false);
        when(queueItemRepository.save(any(QueueItem.class))).thenAnswer(invocation -> {
            QueueItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });

        QueueItemResponse response = service.checkIn(checkInRequest());

        assertThat(response.getQueueNumber()).isEqualTo(1);
        assertThat(response.getStatus()).isEqualTo(QueueStatus.WAITING);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CHECKED_IN);
    }

    @Test
    void startServiceShouldRejectQueueItemThatWasNotCalled() {
        QueueItem item = QueueItem.builder()
                .status(QueueStatus.WAITING)
                .build();
        item.setId(1L);
        when(queueItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> service.startService(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only CALLED queue item can start service");
    }

    private AppointmentCheckInRequest checkInRequest() {
        AppointmentCheckInRequest request = new AppointmentCheckInRequest();
        request.setAppointmentId(1L);
        request.setPriority(QueuePriority.NORMAL);
        return request;
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
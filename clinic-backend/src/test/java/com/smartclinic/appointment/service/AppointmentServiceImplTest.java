package com.smartclinic.appointment.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.smartclinic.appointment.dto.AppointmentRequest;
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
import com.smartclinic.schedule.repository.DoctorAvailabilityRepository;
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
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private DoctorAvailabilityRepository availabilityRepository;

    @InjectMocks
    private AppointmentServiceImpl service;

    @Test
    void createShouldRejectOutsideDoctorAvailability() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient()));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor(true)));
        when(availabilityRepository.existsActiveAvailabilityCovering(eq(1L), eq(1), any(), any()))
                .thenReturn(false);

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Appointment must be inside active doctor availability");
    }

    @Test
    void createShouldRejectDuplicateActiveSlot() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient()));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor(true)));
        when(availabilityRepository.existsActiveAvailabilityCovering(eq(1L), eq(1), any(), any()))
                .thenReturn(true);
        when(appointmentRepository.existsActiveOverlap(eq(1L), any(), any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Doctor already has an active appointment in this slot");
    }

    @Test
    void cancelShouldRejectNonBookedAppointment() {
        Appointment appointment = Appointment.builder()
                .status(AppointmentStatus.CHECKED_IN)
                .build();
        appointment.setId(1L);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.cancel(1L, "Patient changed plan"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only BOOKED appointment can be cancelled");
    }

    private AppointmentRequest request() {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setScheduledStart(LocalDateTime.of(2030, 1, 7, 8, 0));
        request.setScheduledEnd(LocalDateTime.of(2030, 1, 7, 8, 30));
        request.setReason("General consultation");
        request.setSource(AppointmentSource.PHONE);
        return request;
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

    private Doctor doctor(boolean active) {
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
        Room room = Room.builder()
                .id(1L)
                .roomCode("R-101")
                .name("Room 101")
                .active(true)
                .build();
        Specialty specialty = Specialty.builder()
                .id(1L)
                .name("General")
                .active(true)
                .build();
        Doctor doctor = Doctor.builder()
                .staff(staff)
                .specialty(specialty)
                .defaultRoom(room)
                .licenseNo("LIC-001")
                .active(active)
                .build();
        doctor.setId(1L);
        return doctor;
    }
}
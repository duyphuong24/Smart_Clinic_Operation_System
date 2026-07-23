package com.smartclinic.schedule.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.entity.Specialty;
import com.smartclinic.masterdata.repository.RoomRepository;
import com.smartclinic.schedule.dto.DoctorAvailabilityRequest;
import com.smartclinic.schedule.repository.DoctorAvailabilityRepository;
import com.smartclinic.staff.entity.Staff;
import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.entity.StaffType;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DoctorAvailabilityServiceImplTest {

    @Mock
    private DoctorAvailabilityRepository availabilityRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private DoctorAvailabilityServiceImpl service;

    @Test
    void createShouldRejectInvalidTimeRange() {
        DoctorAvailabilityRequest request = request();
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(10, 0));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Availability start time must be before end time");
    }

    @Test
    void createShouldRejectInactiveDoctor() {
        Doctor inactiveDoctor = doctor(false);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(inactiveDoctor));

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Inactive doctor cannot be used for availability");
    }

    @Test
    void createShouldRejectInactiveRoom() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor(true)));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room(false)));

        assertThatThrownBy(() -> service.create(request()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Inactive room cannot be used for availability");
    }

    private DoctorAvailabilityRequest request() {
        DoctorAvailabilityRequest request = new DoctorAvailabilityRequest();
        request.setDoctorId(1L);
        request.setDayOfWeek(1);
        request.setStartTime(LocalTime.of(8, 0));
        request.setEndTime(LocalTime.of(11, 0));
        request.setSlotMinutes(30);
        request.setRoomId(1L);
        request.setActive(true);
        return request;
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
        Specialty specialty = Specialty.builder()
                .id(1L)
                .name("General")
                .active(true)
                .build();
        Doctor doctor = Doctor.builder()
                .staff(staff)
                .specialty(specialty)
                .licenseNo("LIC-001")
                .active(active)
                .build();
        doctor.setId(1L);
        return doctor;
    }

    private Room room(boolean active) {
        return Room.builder()
                .id(1L)
                .roomCode("R-101")
                .name("Room 101")
                .active(active)
                .build();
    }
}
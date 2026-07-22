package com.smartclinic.notification.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.appointment.entity.AppointmentStatus;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.patient.entity.Patient;
import com.smartclinic.staff.entity.Staff;
import com.smartclinic.user.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private EmailService emailService;

    private NotificationServiceImpl notificationService;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(emailService);

        Patient patient = new Patient();
        patient.setFullName("Nguyen Van A");
        patient.setEmail("patient@example.com");

        User doctorUser = User.builder().fullName("BS. Tran Van B").build();
        Staff staff = Staff.builder().user(doctorUser).build();
        Room room = Room.builder().name("Phong 101").build();
        Doctor doctor = Doctor.builder().staff(staff).defaultRoom(room).build();

        appointment = Appointment.builder()
                .appointmentCode("APT-000001")
                .patient(patient)
                .doctor(doctor)
                .room(room)
                .scheduledStart(LocalDateTime.of(2026, 7, 22, 8, 0))
                .scheduledEnd(LocalDateTime.of(2026, 7, 22, 8, 30))
                .reason("Kham suc khoe")
                .status(AppointmentStatus.BOOKED)
                .build();
    }

    @Test
    void sendAppointmentConfirmationShouldCallEmailServiceWhenEmailPresent() {
        notificationService.sendAppointmentConfirmation(appointment);

        verify(emailService).sendHtmlEmail(
                eq("patient@example.com"),
                eq("[Smart Clinic] Xác nhận đặt lịch hẹn thành công - APT-000001"),
                eq("appointment-confirmation"),
                anyMap()
        );
    }

    @Test
    void sendAppointmentReminderShouldCallEmailServiceWhenEmailPresent() {
        notificationService.sendAppointmentReminder(appointment);

        verify(emailService).sendHtmlEmail(
                eq("patient@example.com"),
                eq("[Smart Clinic] Nhắc nhở lịch hẹn khám bệnh ngày mai - APT-000001"),
                eq("appointment-reminder-1day"),
                anyMap()
        );
    }

    @Test
    void sendAppointmentConfirmationShouldSkipWhenPatientEmailMissing() {
        appointment.getPatient().setEmail(null);

        notificationService.sendAppointmentConfirmation(appointment);

        verify(emailService, never()).sendHtmlEmail(any(), any(), any(), any());
    }
}

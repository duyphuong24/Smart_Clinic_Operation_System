package com.smartclinic.notification.scheduler;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.appointment.entity.AppointmentStatus;
import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.notification.service.NotificationService;
import com.smartclinic.patient.entity.Patient;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppointmentReminderSchedulerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentReminderScheduler scheduler;

    private Appointment sampleAppointment;

    @BeforeEach
    void setUp() {
        Patient patient = new Patient();
        patient.setFullName("Nguyen Van A");
        patient.setEmail("patient@example.com");

        sampleAppointment = Appointment.builder()
                .appointmentCode("APT-000001")
                .patient(patient)
                .scheduledStart(LocalDateTime.now().plusDays(1))
                .status(AppointmentStatus.BOOKED)
                .reminderSent(false)
                .build();
    }

    @Test
    void sendUpcomingAppointmentRemindersShouldProcessAppointmentsAndSetReminderSentTrue() {
        when(appointmentRepository.findAppointmentsForReminder(any(), any()))
                .thenReturn(List.of(sampleAppointment));

        scheduler.sendUpcomingAppointmentReminders();

        verify(notificationService).sendAppointmentReminder(sampleAppointment);
        verify(appointmentRepository).save(sampleAppointment);
        assertTrue(sampleAppointment.isReminderSent());
    }
}

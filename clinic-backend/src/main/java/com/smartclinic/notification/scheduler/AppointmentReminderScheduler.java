package com.smartclinic.notification.scheduler;

import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.notification.service.NotificationService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentReminderScheduler {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "${smartclinic.notification.reminder-cron:0 0 8 * * ?}")
    @Transactional
    public void sendUpcomingAppointmentReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime startOfDay = tomorrow.atStartOfDay();
        LocalDateTime endOfDay = tomorrow.atTime(LocalTime.MAX);

        log.info("[AppointmentReminderScheduler] Starting 1-day appointment reminder job for date: {}", tomorrow);

        List<Appointment> upcomingAppointments = appointmentRepository.findAppointmentsForReminder(startOfDay, endOfDay);

        if (upcomingAppointments.isEmpty()) {
            log.info("[AppointmentReminderScheduler] No upcoming appointments found needing reminders for date: {}", tomorrow);
            return;
        }

        log.info("[AppointmentReminderScheduler] Found {} appointment(s) needing reminders.", upcomingAppointments.size());

        for (Appointment appointment : upcomingAppointments) {
            try {
                notificationService.sendAppointmentReminder(appointment);
                appointment.setReminderSent(true);
                appointmentRepository.save(appointment);
                log.info("[AppointmentReminderScheduler] Reminder dispatched and flagged for appointment: {}", appointment.getAppointmentCode());
            } catch (Exception ex) {
                log.error("[AppointmentReminderScheduler] Error processing reminder for appointment {}: {}", appointment.getAppointmentCode(), ex.getMessage(), ex);
            }
        }

        log.info("[AppointmentReminderScheduler] Completed 1-day appointment reminder job.");
    }
}

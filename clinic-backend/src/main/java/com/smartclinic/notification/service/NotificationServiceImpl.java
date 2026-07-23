package com.smartclinic.notification.service;

import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.patient.entity.Patient;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final EmailService emailService;

    @Override
    public void sendAppointmentConfirmation(Appointment appointment) {
        if (appointment == null || appointment.getPatient() == null) {
            return;
        }

        Patient patient = appointment.getPatient();
        String recipientEmail = patient.getEmail();

        if (recipientEmail == null || recipientEmail.isBlank()) {
            log.info("[NotificationService] Patient {} has no email address. Confirmation email skipped.", patient.getFullName());
            return;
        }

        Map<String, Object> variables = prepareTemplateVariables(appointment);
        String subject = "[Smart Clinic] Xác nhận đặt lịch hẹn thành công - " + appointment.getAppointmentCode();

        emailService.sendHtmlEmail(recipientEmail, subject, "appointment-confirmation", variables);
    }

    @Override
    public void sendAppointmentReminder(Appointment appointment) {
        if (appointment == null || appointment.getPatient() == null) {
            return;
        }

        Patient patient = appointment.getPatient();
        String recipientEmail = patient.getEmail();

        if (recipientEmail == null || recipientEmail.isBlank()) {
            log.info("[NotificationService] Patient {} has no email address. Reminder email skipped.", patient.getFullName());
            return;
        }

        Map<String, Object> variables = prepareTemplateVariables(appointment);
        String subject = "[Smart Clinic] Nhắc nhở lịch hẹn khám bệnh ngày mai - " + appointment.getAppointmentCode();

        emailService.sendHtmlEmail(recipientEmail, subject, "appointment-reminder-1day", variables);
    }

    private Map<String, Object> prepareTemplateVariables(Appointment appointment) {
        Map<String, Object> variables = new HashMap<>();
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();

        String doctorName = (doctor != null && doctor.getStaff() != null && doctor.getStaff().getUser() != null)
                ? doctor.getStaff().getUser().getFullName()
                : "Bác sĩ phòng khám";

        String roomName = (appointment.getRoom() != null)
                ? appointment.getRoom().getName()
                : (doctor != null && doctor.getDefaultRoom() != null ? doctor.getDefaultRoom().getName() : "Phòng khám chung");

        String timeRange = appointment.getScheduledStart().format(TIME_FORMATTER) + " - " + appointment.getScheduledEnd().format(TIME_FORMATTER);

        variables.put("patientName", patient.getFullName());
        variables.put("appointmentCode", appointment.getAppointmentCode());
        variables.put("doctorName", doctorName);
        variables.put("roomName", roomName);
        variables.put("scheduledDate", appointment.getScheduledStart().format(DATE_FORMATTER));
        variables.put("scheduledTime", timeRange);
        variables.put("reason", appointment.getReason() != null ? appointment.getReason() : "Khám bệnh");

        return variables;
    }
}

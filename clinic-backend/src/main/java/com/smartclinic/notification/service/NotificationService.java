package com.smartclinic.notification.service;

import com.smartclinic.appointment.entity.Appointment;

public interface NotificationService {

    void sendAppointmentConfirmation(Appointment appointment);

    void sendAppointmentReminder(Appointment appointment);
}

package com.smartclinic.appointment.mapper;

import com.smartclinic.appointment.dto.AppointmentResponse;
import com.smartclinic.appointment.entity.Appointment;

public final class AppointmentMapper {

    private AppointmentMapper() {
    }

    public static AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .appointmentCode(appointment.getAppointmentCode())
                .patientId(appointment.getPatient().getId())
                .patientCode(appointment.getPatient().getPatientCode())
                .patientName(appointment.getPatient().getFullName())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getStaff().getUser().getFullName())
                .roomId(appointment.getRoom().getId())
                .roomCode(appointment.getRoom().getRoomCode())
                .scheduledStart(appointment.getScheduledStart())
                .scheduledEnd(appointment.getScheduledEnd())
                .reason(appointment.getReason())
                .source(appointment.getSource())
                .status(appointment.getStatus())
                .cancelledReason(appointment.getCancelledReason())
                .build();
    }
}
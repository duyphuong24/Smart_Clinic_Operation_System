package com.smartclinic.appointment.dto;

import com.smartclinic.appointment.entity.AppointmentSource;
import com.smartclinic.appointment.entity.AppointmentStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppointmentResponse {

    private final Long id;
    private final String appointmentCode;
    private final Long patientId;
    private final String patientCode;
    private final String patientName;
    private final Long doctorId;
    private final String doctorName;
    private final Long roomId;
    private final String roomCode;
    private final LocalDateTime scheduledStart;
    private final LocalDateTime scheduledEnd;
    private final String reason;
    private final AppointmentSource source;
    private final AppointmentStatus status;
    private final String cancelledReason;
}
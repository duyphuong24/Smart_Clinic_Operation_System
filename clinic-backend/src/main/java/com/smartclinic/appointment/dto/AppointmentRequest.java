package com.smartclinic.appointment.dto;

import com.smartclinic.appointment.entity.AppointmentSource;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentRequest {

    @NotNull
    private Long patientId;

    @NotNull
    private Long doctorId;

    private Long roomId;

    @NotNull
    @FutureOrPresent
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime scheduledStart;

    @NotNull
    @FutureOrPresent
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime scheduledEnd;

    private String reason;

    @NotNull
    private AppointmentSource source;
}
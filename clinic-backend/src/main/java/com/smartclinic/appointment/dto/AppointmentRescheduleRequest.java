package com.smartclinic.appointment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentRescheduleRequest {

    private Long doctorId;

    private Long roomId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime scheduledStart;

    @NotNull
    @FutureOrPresent
    private LocalDateTime scheduledEnd;
}
package com.smartclinic.schedule.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorAvailabilityRequest {

    @NotNull
    private Long doctorId;

    @NotNull
    @Min(1)
    @Max(7)
    private Integer dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    @Min(1)
    private Integer slotMinutes;

    @NotNull
    private Long roomId;

    private boolean active = true;
}
package com.smartclinic.schedule.dto;

import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorAvailabilityResponse {

    private final Long id;
    private final Long doctorId;
    private final String doctorName;
    private final Integer dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Integer slotMinutes;
    private final Long roomId;
    private final String roomCode;
    private final boolean active;
}
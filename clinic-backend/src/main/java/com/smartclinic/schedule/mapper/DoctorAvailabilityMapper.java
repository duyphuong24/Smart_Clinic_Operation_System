package com.smartclinic.schedule.mapper;

import com.smartclinic.schedule.dto.DoctorAvailabilityResponse;
import com.smartclinic.schedule.entity.DoctorAvailability;

public final class DoctorAvailabilityMapper {

    private DoctorAvailabilityMapper() {
    }

    public static DoctorAvailabilityResponse toResponse(DoctorAvailability availability) {
        return DoctorAvailabilityResponse.builder()
                .id(availability.getId())
                .doctorId(availability.getDoctor().getId())
                .doctorName(availability.getDoctor().getStaff().getUser().getFullName())
                .dayOfWeek(availability.getDayOfWeek())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .slotMinutes(availability.getSlotMinutes())
                .roomId(availability.getRoom().getId())
                .roomCode(availability.getRoom().getRoomCode())
                .active(availability.isActive())
                .build();
    }
}
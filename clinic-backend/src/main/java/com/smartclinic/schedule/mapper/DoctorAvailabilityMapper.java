package com.smartclinic.schedule.mapper;

import com.smartclinic.schedule.dto.DoctorAvailabilityResponse;
import com.smartclinic.schedule.entity.DoctorAvailability;

public final class DoctorAvailabilityMapper {

    private DoctorAvailabilityMapper() {
    }

    public static DoctorAvailabilityResponse toResponse(DoctorAvailability availability) {
        String specialtyName = null;
        if (availability.getDoctor() != null && availability.getDoctor().getSpecialty() != null) {
            specialtyName = availability.getDoctor().getSpecialty().getName();
        }

        String roomName = null;
        String roomCode = null;
        if (availability.getRoom() != null) {
            roomName = availability.getRoom().getName();
            roomCode = availability.getRoom().getRoomCode();
        }

        String doctorName = null;
        if (availability.getDoctor() != null 
                && availability.getDoctor().getStaff() != null 
                && availability.getDoctor().getStaff().getUser() != null) {
            doctorName = availability.getDoctor().getStaff().getUser().getFullName();
        }

        return DoctorAvailabilityResponse.builder()
                .id(availability.getId())
                .doctorId(availability.getDoctor() != null ? availability.getDoctor().getId() : null)
                .doctorName(doctorName)
                .specialtyName(specialtyName)
                .dayOfWeek(availability.getDayOfWeek())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .slotMinutes(availability.getSlotMinutes())
                .roomId(availability.getRoom() != null ? availability.getRoom().getId() : null)
                .roomCode(roomCode)
                .roomName(roomName)
                .active(availability.isActive())
                .build();
    }
}
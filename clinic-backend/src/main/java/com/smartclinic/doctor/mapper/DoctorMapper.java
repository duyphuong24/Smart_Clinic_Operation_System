package com.smartclinic.doctor.mapper;

import com.smartclinic.doctor.dto.DoctorResponse;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.masterdata.entity.Room;

public final class DoctorMapper {

    private DoctorMapper() {
    }

    public static DoctorResponse toResponse(Doctor doctor) {
        Room room = doctor.getDefaultRoom();
        return DoctorResponse.builder()
                .id(doctor.getId())
                .staffId(doctor.getStaff().getId())
                .employeeCode(doctor.getStaff().getEmployeeCode())
                .fullName(doctor.getStaff().getUser().getFullName())
                .specialtyId(doctor.getSpecialty().getId())
                .specialtyName(doctor.getSpecialty().getName())
                .defaultRoomId(room == null ? null : room.getId())
                .defaultRoomCode(room == null ? null : room.getRoomCode())
                .licenseNo(doctor.getLicenseNo())
                .consultationFee(doctor.getConsultationFee())
                .bio(doctor.getBio())
                .active(doctor.isActive())
                .build();
    }
}
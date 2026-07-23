package com.smartclinic.staff.mapper;

import com.smartclinic.staff.dto.StaffResponse;
import com.smartclinic.staff.entity.Staff;

public final class StaffMapper {

    private StaffMapper() {
    }

    public static StaffResponse toResponse(Staff staff) {
        return StaffResponse.builder()
                .id(staff.getId())
                .userId(staff.getUser().getId())
                .userName(staff.getUser().getUserName())
                .fullName(staff.getUser().getFullName())
                .employeeCode(staff.getEmployeeCode())
                .staffType(staff.getStaffType())
                .hiredDate(staff.getHiredDate())
                .status(staff.getStatus())
                .build();
    }
}
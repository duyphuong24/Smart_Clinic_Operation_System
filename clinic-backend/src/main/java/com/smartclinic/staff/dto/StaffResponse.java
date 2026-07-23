package com.smartclinic.staff.dto;

import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.entity.StaffType;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffResponse {

    private final Long id;
    private final Long userId;
    private final String userName;
    private final String fullName;
    private final String employeeCode;
    private final StaffType staffType;
    private final LocalDate hiredDate;
    private final StaffStatus status;
}
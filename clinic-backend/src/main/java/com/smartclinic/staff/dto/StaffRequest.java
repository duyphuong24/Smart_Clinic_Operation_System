package com.smartclinic.staff.dto;

import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.entity.StaffType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String employeeCode;

    @NotNull
    private StaffType staffType;

    private LocalDate hiredDate;

    private StaffStatus status = StaffStatus.ACTIVE;
}
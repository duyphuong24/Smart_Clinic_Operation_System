package com.smartclinic.doctor.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorResponse {

    private final Long id;
    private final Long staffId;
    private final String employeeCode;
    private final String fullName;
    private final Long specialtyId;
    private final String specialtyName;
    private final Long defaultRoomId;
    private final String defaultRoomCode;
    private final String licenseNo;
    private final BigDecimal consultationFee;
    private final String bio;
    private final boolean active;
}
package com.smartclinic.doctor.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorRequest {

    @NotNull
    private Long staffId;

    @NotNull
    private Long specialtyId;

    private Long defaultRoomId;

    @NotBlank
    private String licenseNo;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal consultationFee;

    private String bio;

    private boolean active = true;
}
package com.smartclinic.patient.dto;

import com.smartclinic.patient.entity.Gender;
import com.smartclinic.patient.entity.PatientStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PatientResponse {

    private final Long id;
    private final String patientCode;
    private final String fullName;
    private final LocalDate dateOfBirth;
    private final Gender gender;
    private final String phone;
    private final String email;
    private final String address;
    private final String identityNumber;
    private final String emergencyContactName;
    private final String emergencyContactPhone;
    private final String allergyNote;
    private final PatientStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
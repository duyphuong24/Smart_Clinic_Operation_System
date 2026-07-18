package com.smartclinic.patient.dto;

import com.smartclinic.patient.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientCreateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must be at most 150 characters")
    private String fullName;

    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 30, message = "Phone must be at most 30 characters")
    private String phone;

    @Email(message = "Email format is invalid")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String email;

    private String address;

    @Size(max = 50, message = "Identity number must be at most 50 characters")
    private String identityNumber;

    @Size(max = 150, message = "Emergency contact name must be at most 150 characters")
    private String emergencyContactName;

    @Size(max = 30, message = "Emergency contact phone must be at most 30 characters")
    private String emergencyContactPhone;

    private String allergyNote;
}
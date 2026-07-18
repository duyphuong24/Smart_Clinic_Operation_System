package com.smartclinic.patient.entity;

import com.smartclinic.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "patients",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_patients_patient_code", columnNames = "patient_code")
        }
)
@Getter
@Setter
public class Patient extends BaseEntity {

    @Column(name = "patient_code", nullable = false, unique = true, length = 50)
    private String patientCode;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "address", columnDefinition = "NVARCHAR(MAX)")
    private String address;

    @Column(name = "identity_number", length = 50)
    private String identityNumber;

    @Column(name = "emergency_contact_name", length = 150)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 30)
    private String emergencyContactPhone;

    @Column(name = "allergy_note", columnDefinition = "NVARCHAR(MAX)")
    private String allergyNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PatientStatus status = PatientStatus.ACTIVE;
}
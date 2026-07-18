package com.smartclinic.patient.mapper;

import com.smartclinic.patient.dto.PatientCreateRequest;
import com.smartclinic.patient.dto.PatientResponse;
import com.smartclinic.patient.entity.Patient;

public final class PatientMapper {

    private PatientMapper() {
    }

    public static Patient toEntity(PatientCreateRequest request) {
        Patient patient = new Patient();
        apply(patient, request);
        return patient;
    }

    public static void apply(Patient patient, PatientCreateRequest request) {
        patient.setFullName(trim(request.getFullName()));
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhone(trim(request.getPhone()));
        patient.setEmail(trim(request.getEmail()));
        patient.setAddress(trim(request.getAddress()));
        patient.setIdentityNumber(trim(request.getIdentityNumber()));
        patient.setEmergencyContactName(trim(request.getEmergencyContactName()));
        patient.setEmergencyContactPhone(trim(request.getEmergencyContactPhone()));
        patient.setAllergyNote(trim(request.getAllergyNote()));
    }

    public static PatientResponse toResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .patientCode(patient.getPatientCode())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .identityNumber(patient.getIdentityNumber())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .allergyNote(patient.getAllergyNote())
                .status(patient.getStatus())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
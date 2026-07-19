package com.smartclinic.desktop.util;

import com.smartclinic.desktop.dto.PatientResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class PatientUiUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private PatientUiUtil() {
    }

    public static String formatDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return "-";
        }
        return DATE_FORMAT.format(dateOfBirth);
    }

    public static String formatGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return "-";
        }
        return gender.charAt(0) + gender.substring(1).toLowerCase();
    }

    public static String formatValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    public static String displayStatus(String status) {
        if (status == null || status.isBlank()) {
            return "UNKNOWN";
        }
        return status.charAt(0) + status.substring(1).toLowerCase();
    }

    public static String statusStyleClass(String status) {
        if (status == null) {
            return "patient-status-default";
        }
        return switch (status) {
            case "ACTIVE" -> "patient-status-active";
            case "ARCHIVED" -> "patient-status-archived";
            default -> "patient-status-default";
        };
    }

    public static String formatPatientLabel(PatientResponse patient) {
        if (patient == null) {
            return "-";
        }
        String name = patient.getFullName() == null || patient.getFullName().isBlank()
                ? "Unknown"
                : patient.getFullName();
        String code = patient.getPatientCode();
        if (code == null || code.isBlank()) {
            return name;
        }
        return name + " (" + code + ")";
    }
}

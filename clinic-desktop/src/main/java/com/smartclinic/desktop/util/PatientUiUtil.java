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

    public static String buildDetailSummary(PatientResponse patient) {
        return "Phone: " + formatValue(patient.getPhone())
                + " | Email: " + formatValue(patient.getEmail())
                + " | Address: " + formatValue(patient.getAddress());
    }
}

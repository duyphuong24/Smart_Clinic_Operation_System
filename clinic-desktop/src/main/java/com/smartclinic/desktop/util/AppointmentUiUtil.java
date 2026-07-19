package com.smartclinic.desktop.util;

import com.smartclinic.desktop.dto.AppointmentResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class AppointmentUiUtil {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private AppointmentUiUtil() {
    }

    public static String formatTimeSlot(AppointmentResponse appointment) {
        if (appointment.getScheduledStart() == null || appointment.getScheduledEnd() == null) {
            return "-";
        }
        return TIME_FORMAT.format(appointment.getScheduledStart())
                + " - "
                + TIME_FORMAT.format(appointment.getScheduledEnd());
    }

    public static String formatPatient(AppointmentResponse appointment) {
        String name = appointment.getPatientName() == null ? "Unknown" : appointment.getPatientName();
        String code = appointment.getPatientCode();
        if (code == null || code.isBlank()) {
            return name;
        }
        return name + " (" + code + ")";
    }

    public static String formatRoom(AppointmentResponse appointment) {
        String roomCode = appointment.getRoomCode();
        return roomCode == null || roomCode.isBlank() ? "-" : roomCode;
    }

    public static String displayStatus(String status) {
        if (status == null || status.isBlank()) {
            return "UNKNOWN";
        }
        return status.replace('_', ' ');
    }

    public static String statusStyleClass(String status) {
        if (status == null) {
            return "status-badge-default";
        }
        return switch (status) {
            case "BOOKED" -> "status-badge-booked";
            case "CHECKED_IN" -> "status-badge-checked-in";
            case "IN_CONSULTATION" -> "status-badge-in-consultation";
            case "COMPLETED" -> "status-badge-completed";
            case "CANCELLED" -> "status-badge-cancelled";
            case "NO_SHOW" -> "status-badge-no-show";
            default -> "status-badge-default";
        };
    }

    public static List<AppointmentResponse> filter(
            List<AppointmentResponse> appointments,
            String statusFilter,
            String keyword
    ) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        boolean filterByStatus = statusFilter != null && !statusFilter.isBlank() && !"ALL".equals(statusFilter);

        return appointments.stream()
                .filter(appointment -> !filterByStatus || statusFilter.equals(appointment.getStatus()))
                .filter(appointment -> matchesKeyword(appointment, normalizedKeyword))
                .toList();
    }

    private static boolean matchesKeyword(AppointmentResponse appointment, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }
        return containsIgnoreCase(appointment.getAppointmentCode(), keyword)
                || containsIgnoreCase(appointment.getPatientName(), keyword)
                || containsIgnoreCase(appointment.getPatientCode(), keyword)
                || containsIgnoreCase(appointment.getDoctorName(), keyword);
    }

    private static boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }
}

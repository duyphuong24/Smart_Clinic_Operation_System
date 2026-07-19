package com.smartclinic.desktop.util;

import com.smartclinic.desktop.dto.QueueItemResponse;
import java.util.List;
import java.util.Locale;

public final class QueueUiUtil {

    private QueueUiUtil() {
    }

    public static String formatPatient(QueueItemResponse item) {
        if (item == null) {
            return "-";
        }
        String name = item.getPatientName() == null || item.getPatientName().isBlank()
                ? "Unknown"
                : item.getPatientName();
        String code = item.getPatientCode();
        if (code == null || code.isBlank()) {
            return name;
        }
        return name + " (" + code + ")";
    }

    public static String formatRoom(QueueItemResponse item) {
        if (item == null || item.getRoomCode() == null || item.getRoomCode().isBlank()) {
            return "-";
        }
        return item.getRoomCode();
    }

    public static String displayStatus(String status) {
        if (status == null || status.isBlank()) {
            return "UNKNOWN";
        }
        return status.replace('_', ' ');
    }

    public static String displayPriority(String priority) {
        if (priority == null || priority.isBlank()) {
            return "NORMAL";
        }
        return priority.charAt(0) + priority.substring(1).toLowerCase(Locale.ROOT);
    }

    public static String statusStyleClass(String status) {
        if (status == null) {
            return "queue-status-default";
        }
        return switch (status) {
            case "WAITING" -> "queue-status-waiting";
            case "CALLED" -> "queue-status-called";
            case "IN_SERVICE" -> "queue-status-in-service";
            case "DONE" -> "queue-status-done";
            case "SKIPPED" -> "queue-status-skipped";
            default -> "queue-status-default";
        };
    }

    public static String priorityStyleClass(String priority) {
        if (priority == null || "NORMAL".equals(priority)) {
            return "queue-priority-normal";
        }
        return "queue-priority-urgent";
    }

    public static boolean canCall(List<String> roles, String status) {
        return "WAITING".equals(status)
                && RoleUtil.hasAnyRole(roles, "ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_DOCTOR");
    }

    public static boolean canStart(List<String> roles, String status) {
        return "CALLED".equals(status)
                && RoleUtil.hasAnyRole(roles, "ROLE_ADMIN", "ROLE_DOCTOR");
    }

    public static boolean canDone(List<String> roles, String status) {
        return "IN_SERVICE".equals(status)
                && RoleUtil.hasAnyRole(roles, "ROLE_ADMIN", "ROLE_DOCTOR");
    }

    public static boolean canSkip(List<String> roles, String status) {
        return ("WAITING".equals(status) || "CALLED".equals(status))
                && RoleUtil.hasAnyRole(roles, "ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_DOCTOR");
    }
}

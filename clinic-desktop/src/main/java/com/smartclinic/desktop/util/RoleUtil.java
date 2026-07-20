package com.smartclinic.desktop.util;

import java.util.List;
import java.util.Locale;

public final class RoleUtil {

    private RoleUtil() {
    }

    public static String displayName(String role) {
        if (role == null || role.isBlank()) {
            return "User";
        }
        String normalized = role.startsWith("ROLE_") ? role.substring(5) : role;
        return switch (normalized) {
            case "ADMIN" -> "Administrator";
            case "RECEPTIONIST" -> "Receptionist";
            case "DOCTOR" -> "Doctor";
            case "CASHIER" -> "Cashier";
            case "MANAGER" -> "Manager";
            default -> toTitleCase(normalized.replace('_', ' '));
        };
    }

    public static String primaryRoleLabel(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return "User";
        }
        return displayName(roles.getFirst());
    }

    public static boolean hasAnyRole(List<String> roles, String... expectedRoles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        for (String expected : expectedRoles) {
            if (roles.contains(expected)) {
                return true;
            }
        }
        return false;
    }

    private static String toTitleCase(String value) {
        if (value.isBlank()) {
            return value;
        }
        String lower = value.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}

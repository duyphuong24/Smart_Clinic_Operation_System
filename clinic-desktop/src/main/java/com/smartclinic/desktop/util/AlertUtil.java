package com.smartclinic.desktop.util;

import com.smartclinic.desktop.api.ApiException;
import java.util.concurrent.CompletionException;
import javafx.scene.control.Alert;

public final class AlertUtil {

    private AlertUtil() {
    }

    public static String userMessage(Throwable throwable) {
        Throwable root = unwrap(throwable);
        if (root instanceof ApiException apiException) {
            return switch (apiException.getStatusCode()) {
                case 400 -> apiException.getMessage();
                case 401 -> "Login is required or your session has expired.";
                case 403 -> "You do not have permission to perform this action.";
                case 404 -> "Requested data was not found.";
                case 409 -> apiException.getMessage();
                case 422 -> apiException.getMessage();
                case 500 -> "Server error. Please try again later.";
                default -> apiException.getMessage();
            };
        }
        return root.getMessage() == null ? "Unexpected error." : root.getMessage();
    }

    public static void showError(String title, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(userMessage(throwable));
        alert.showAndWait();
    }

    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static Throwable unwrap(Throwable throwable) {
        if (throwable instanceof CompletionException && throwable.getCause() != null) {
            return throwable.getCause();
        }
        return throwable;
    }
}
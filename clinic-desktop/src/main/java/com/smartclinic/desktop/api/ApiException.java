package com.smartclinic.desktop.api;

import com.smartclinic.desktop.dto.ApiResponse;

public class ApiException extends RuntimeException {

    private final int statusCode;
    private final ApiResponse<?> response;

    public ApiException(int statusCode, ApiResponse<?> response) {
        super(resolveMessage(statusCode, response));
        this.statusCode = statusCode;
        this.response = response;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.response = null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ApiResponse<?> getResponse() {
        return response;
    }

    private static String resolveMessage(int statusCode, ApiResponse<?> response) {
        if (response != null && response.getMessage() != null && !response.getMessage().isBlank()) {
            return response.getMessage();
        }
        return "Request failed with status " + statusCode;
    }
}
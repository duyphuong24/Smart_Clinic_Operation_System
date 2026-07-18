package com.smartclinic.common.api;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final List<FieldErrorResponse> errors;
    private final LocalDateTime timestamp;
    private final String path;

    private ApiResponse(
            boolean success,
            String message,
            T data,
            List<FieldErrorResponse> errors,
            String path
    ) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }

    public static <T> ApiResponse<T> success(String message, T data, String path) {
        return new ApiResponse<>(true, message, data, null, path);
    }

    public static <T> ApiResponse<T> created(String message, T data, String path) {
        return success(message, data, path);
    }

    public static ApiResponse<Void> success(String message, String path) {
        return new ApiResponse<>(true, message, null, null, path);
    }

    public static ApiResponse<Void> error(String message, String path) {
        return new ApiResponse<>(false, message, null, null, path);
    }

    public static ApiResponse<Void> validationError(
            String message,
            List<FieldErrorResponse> errors,
            String path
    ) {
        return new ApiResponse<>(false, message, null, errors, path);
    }
}

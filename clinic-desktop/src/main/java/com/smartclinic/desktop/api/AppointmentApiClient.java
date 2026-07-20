package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.AppointmentResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AppointmentApiClient {

    private static final DateTimeFormatter DATE_PARAM = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ApiClient apiClient;

    public AppointmentApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<List<AppointmentResponse>>> findByDate(LocalDate date) {
        String path = "/appointments?date=" + DATE_PARAM.format(date);
        return apiClient.get(
                path,
                new TypeReference<ApiResponse<List<AppointmentResponse>>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<AppointmentResponse>> create(Object requestBody) {
        return apiClient.post(
                "/appointments",
                requestBody,
                new TypeReference<ApiResponse<AppointmentResponse>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<AppointmentResponse>> cancel(Long id, String reason) {
        return apiClient.patch(
                "/appointments/" + id + "/cancel",
                new com.smartclinic.desktop.dto.CancelAppointmentRequest(reason),
                new TypeReference<ApiResponse<AppointmentResponse>>() {},
                true
        );
    }
}

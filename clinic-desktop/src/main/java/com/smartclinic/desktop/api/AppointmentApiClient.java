package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.AppointmentResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AppointmentApiClient {

    private final ApiClient apiClient;

    public AppointmentApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<List<AppointmentResponse>>> getTodayAppointments() {
        return apiClient.get(
                "/appointments/today",
                new TypeReference<ApiResponse<List<AppointmentResponse>>>() {},
                true
        );
    }
}

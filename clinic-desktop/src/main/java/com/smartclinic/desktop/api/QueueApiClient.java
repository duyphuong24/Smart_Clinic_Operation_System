package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.AppointmentCheckInRequest;
import com.smartclinic.desktop.dto.QueueItemResponse;
import java.util.concurrent.CompletableFuture;

public class QueueApiClient {

    private final ApiClient apiClient;

    public QueueApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<QueueItemResponse>> checkIn(AppointmentCheckInRequest request) {
        return apiClient.post(
                "/queue-items/check-in",
                request,
                new TypeReference<ApiResponse<QueueItemResponse>>() {},
                true
        );
    }
}

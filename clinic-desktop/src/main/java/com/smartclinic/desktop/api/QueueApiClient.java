package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.AppointmentCheckInRequest;
import com.smartclinic.desktop.dto.QueueItemResponse;
import com.smartclinic.desktop.dto.WalkInQueueRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QueueApiClient {

    private static final DateTimeFormatter DATE_PARAM = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ApiClient apiClient;

    public QueueApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<List<QueueItemResponse>>> findActive(LocalDate date, Long doctorId) {
        StringBuilder path = new StringBuilder("/queue-items?date=").append(DATE_PARAM.format(date));
        if (doctorId != null) {
            path.append("&doctorId=").append(doctorId);
        }
        return apiClient.get(
                path.toString(),
                new TypeReference<ApiResponse<List<QueueItemResponse>>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<QueueItemResponse>> checkIn(AppointmentCheckInRequest request) {
        return apiClient.post(
                "/queue-items/check-in",
                request,
                new TypeReference<ApiResponse<QueueItemResponse>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<QueueItemResponse>> createWalkIn(WalkInQueueRequest request) {
        return apiClient.post(
                "/queue-items/walk-in",
                request,
                new TypeReference<ApiResponse<QueueItemResponse>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<QueueItemResponse>> call(Long id) {
        return apiClient.patch(
                "/queue-items/" + id + "/call",
                new TypeReference<ApiResponse<QueueItemResponse>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<QueueItemResponse>> startService(Long id) {
        return apiClient.patch(
                "/queue-items/" + id + "/start-service",
                new TypeReference<ApiResponse<QueueItemResponse>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<QueueItemResponse>> done(Long id) {
        return apiClient.patch(
                "/queue-items/" + id + "/done",
                new TypeReference<ApiResponse<QueueItemResponse>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<QueueItemResponse>> skip(Long id) {
        return skip(id, null);
    }

    public CompletableFuture<ApiResponse<QueueItemResponse>> skip(Long id, String reason) {
        String path = "/queue-items/" + id + "/skip";
        if (reason != null && !reason.isBlank()) {
            path += "?reason=" + java.net.URLEncoder.encode(reason, java.nio.charset.StandardCharsets.UTF_8);
        }
        return apiClient.patch(
                path,
                new TypeReference<ApiResponse<QueueItemResponse>>() {},
                true
        );
    }
}

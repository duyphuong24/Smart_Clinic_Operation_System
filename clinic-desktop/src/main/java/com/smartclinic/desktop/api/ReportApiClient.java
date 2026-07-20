package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.DashboardMetricsResponse;
import java.util.concurrent.CompletableFuture;

public class ReportApiClient {

    private final ApiClient apiClient;

    public ReportApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<DashboardMetricsResponse>> getDashboardMetrics() {
        return apiClient.get(
                "/reports/dashboard",
                new TypeReference<ApiResponse<DashboardMetricsResponse>>() {},
                true
        );
    }
}

package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.DoctorResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DoctorApiClient {

    private final ApiClient apiClient;

    public DoctorApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<List<DoctorResponse>>> findAll() {
        return apiClient.get(
                "/doctors",
                new TypeReference<ApiResponse<List<DoctorResponse>>>() {},
                true
        );
    }
}

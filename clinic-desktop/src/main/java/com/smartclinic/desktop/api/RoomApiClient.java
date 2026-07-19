package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.RoomResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoomApiClient {

    private final ApiClient apiClient;

    public RoomApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<List<RoomResponse>>> findAll() {
        return apiClient.get(
                "/rooms",
                new TypeReference<ApiResponse<List<RoomResponse>>>() {},
                true
        );
    }
}

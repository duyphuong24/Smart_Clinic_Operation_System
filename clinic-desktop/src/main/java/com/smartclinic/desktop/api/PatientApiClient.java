package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.PageResponse;
import com.smartclinic.desktop.dto.PatientResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class PatientApiClient {

    private final ApiClient apiClient;

    public PatientApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<PageResponse<PatientResponse>>> search(String keyword, int page, int size) {
        String encodedKeyword = URLEncoder.encode(keyword == null ? "" : keyword, StandardCharsets.UTF_8);
        String path = "/patients?keyword=" + encodedKeyword + "&page=" + page + "&size=" + size;
        return apiClient.get(
                path,
                new TypeReference<ApiResponse<PageResponse<PatientResponse>>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<PatientResponse>> getById(Long id) {
        return apiClient.get(
                "/patients/" + id,
                new TypeReference<ApiResponse<PatientResponse>>() {},
                true
        );
    }
}

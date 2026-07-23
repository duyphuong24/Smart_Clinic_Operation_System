package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.CurrentUserResponse;
import com.smartclinic.desktop.dto.LoginRequest;
import com.smartclinic.desktop.dto.LoginResponse;
import java.util.concurrent.CompletableFuture;

public class AuthApiClient {

    private final ApiClient apiClient;

    public AuthApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<LoginResponse>> login(LoginRequest request) {
        return apiClient.post(
                "/auth/login",
                request,
                new TypeReference<ApiResponse<LoginResponse>>() {},
                false
        );
    }

    public CompletableFuture<ApiResponse<CurrentUserResponse>> me() {
        return apiClient.get(
                "/auth/me",
                new TypeReference<ApiResponse<CurrentUserResponse>>() {},
                true
        );
    }
}
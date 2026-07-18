package com.smartclinic.desktop.service;

import com.smartclinic.desktop.api.AuthApiClient;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.CurrentUserResponse;
import com.smartclinic.desktop.dto.LoginRequest;
import com.smartclinic.desktop.dto.LoginResponse;
import com.smartclinic.desktop.session.SessionManager;
import java.util.concurrent.CompletableFuture;

public class AuthService {

    private final AuthApiClient authApiClient;
    private final SessionManager sessionManager;

    public AuthService(AuthApiClient authApiClient, SessionManager sessionManager) {
        this.authApiClient = authApiClient;
        this.sessionManager = sessionManager;
    }

    public CompletableFuture<LoginResponse> login(String userName, String password) {
        return authApiClient.login(new LoginRequest(userName, password))
                .thenApply(ApiResponse::getData)
                .thenApply(response -> {
                    sessionManager.startSession(response);
                    return response;
                });
    }

    public CompletableFuture<CurrentUserResponse> currentUser() {
        return authApiClient.me().thenApply(ApiResponse::getData);
    }

    public void logout() {
        sessionManager.clear();
    }
}
package com.smartclinic.desktop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.desktop.api.ApiClient;
import com.smartclinic.desktop.api.AuthApiClient;
import com.smartclinic.desktop.controller.LoginController;
import com.smartclinic.desktop.service.AuthService;
import com.smartclinic.desktop.session.SessionManager;
import java.lang.reflect.InvocationTargetException;
import java.net.http.HttpClient;

public class AppContext {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api/v1";

    private final SessionManager sessionManager;
    private final AuthService authService;

    public AppContext() {
        this.sessionManager = new SessionManager();
        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiClient apiClient = new ApiClient(httpClient, objectMapper, sessionManager, resolveBaseUrl());
        AuthApiClient authApiClient = new AuthApiClient(apiClient);
        this.authService = new AuthService(authApiClient, sessionManager);
    }

    public Object createController(Class<?> controllerClass) {
        if (controllerClass == LoginController.class) {
            return new LoginController(authService);
        }

        try {
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new IllegalStateException("Unable to create controller: " + controllerClass.getName(), ex);
        }
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public AuthService getAuthService() {
        return authService;
    }

    private String resolveBaseUrl() {
        String propertyValue = System.getProperty("smartclinic.api.base-url");
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String envValue = System.getenv("SMARTCLINIC_API_BASE_URL");
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return DEFAULT_BASE_URL;
    }
}
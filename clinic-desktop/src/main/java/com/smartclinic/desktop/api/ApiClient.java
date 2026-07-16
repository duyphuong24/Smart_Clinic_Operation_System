package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.session.SessionManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ApiClient {

    private static final String CONTENT_TYPE = "application/json";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;
    private final String baseUrl;

    public ApiClient(HttpClient httpClient, ObjectMapper objectMapper, SessionManager sessionManager, String baseUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.sessionManager = sessionManager;
        this.baseUrl = normalizeBaseUrl(baseUrl);
    }

    public <T> CompletableFuture<ApiResponse<T>> get(
            String path,
            TypeReference<ApiResponse<T>> responseType,
            boolean authenticated
    ) {
        HttpRequest.Builder builder = baseRequest(path).GET();
        addAuthorization(builder, authenticated);
        return send(builder.build(), responseType);
    }

    public <T> CompletableFuture<ApiResponse<T>> post(
            String path,
            Object body,
            TypeReference<ApiResponse<T>> responseType,
            boolean authenticated
    ) {
        String json = toJson(body);
        HttpRequest.Builder builder = baseRequest(path)
                .POST(HttpRequest.BodyPublishers.ofString(json));
        addAuthorization(builder, authenticated);
        return send(builder.build(), responseType);
    }

    private <T> CompletableFuture<ApiResponse<T>> send(
            HttpRequest request,
            TypeReference<ApiResponse<T>> responseType
    ) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseResponse(response, responseType));
    }

    private <T> ApiResponse<T> parseResponse(
            HttpResponse<String> response,
            TypeReference<ApiResponse<T>> responseType
    ) {
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return readBody(response.body(), responseType);
        }

        ApiResponse<Void> errorResponse;
        try {
            errorResponse = objectMapper.readValue(response.body(), new TypeReference<ApiResponse<Void>>() {});
        } catch (IOException ex) {
            errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Request failed with status " + statusCode);
        }
        throw new ApiException(statusCode, errorResponse);
    }

    private <T> ApiResponse<T> readBody(String body, TypeReference<ApiResponse<T>> responseType) {
        try {
            return objectMapper.readValue(body, responseType);
        } catch (IOException ex) {
            throw new ApiException("Unable to parse server response", ex);
        }
    }

    private String toJson(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException ex) {
            throw new ApiException("Unable to serialize request body", ex);
        }
    }

    private HttpRequest.Builder baseRequest(String path) {
        return HttpRequest.newBuilder(URI.create(baseUrl + normalizePath(path)))
                .header("Accept", CONTENT_TYPE)
                .header("Content-Type", CONTENT_TYPE);
    }

    private void addAuthorization(HttpRequest.Builder builder, boolean authenticated) {
        if (!authenticated) {
            return;
        }
        sessionManager.authorizationHeader()
                .ifPresent(value -> builder.header("Authorization", value));
    }

    private String normalizeBaseUrl(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    private String normalizePath(String path) {
        if (path.startsWith("/")) {
            return path;
        }
        return "/" + path;
    }
}
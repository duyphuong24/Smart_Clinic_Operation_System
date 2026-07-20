package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.LoginResponse;
import com.smartclinic.desktop.dto.TokenRefreshRequest;
import com.smartclinic.desktop.session.SessionManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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
        Supplier<HttpRequest> requestSupplier = () -> {
            HttpRequest.Builder builder = baseRequest(path).GET();
            addAuthorization(builder, authenticated);
            return builder.build();
        };
        return sendWithRetry(requestSupplier, responseType, authenticated);
    }

    public <T> CompletableFuture<ApiResponse<T>> post(
            String path,
            Object body,
            TypeReference<ApiResponse<T>> responseType,
            boolean authenticated
    ) {
        Supplier<HttpRequest> requestSupplier = () -> {
            String json = toJson(body);
            HttpRequest.Builder builder = baseRequest(path)
                    .POST(HttpRequest.BodyPublishers.ofString(json));
            addAuthorization(builder, authenticated);
            return builder.build();
        };
        return sendWithRetry(requestSupplier, responseType, authenticated);
    }

    public <T> CompletableFuture<ApiResponse<T>> put(
            String path,
            Object body,
            TypeReference<ApiResponse<T>> responseType,
            boolean authenticated
    ) {
        Supplier<HttpRequest> requestSupplier = () -> {
            String json = toJson(body);
            HttpRequest.Builder builder = baseRequest(path)
                    .PUT(HttpRequest.BodyPublishers.ofString(json));
            addAuthorization(builder, authenticated);
            return builder.build();
        };
        return sendWithRetry(requestSupplier, responseType, authenticated);
    }

    public <T> CompletableFuture<ApiResponse<T>> patch(
            String path,
            TypeReference<ApiResponse<T>> responseType,
            boolean authenticated
    ) {
        Supplier<HttpRequest> requestSupplier = () -> {
            HttpRequest.Builder builder = baseRequest(path)
                    .method("PATCH", HttpRequest.BodyPublishers.noBody());
            addAuthorization(builder, authenticated);
            return builder.build();
        };
        return sendWithRetry(requestSupplier, responseType, authenticated);
    }

    public <T> CompletableFuture<ApiResponse<T>> patch(
            String path,
            Object body,
            TypeReference<ApiResponse<T>> responseType,
            boolean authenticated
    ) {
        Supplier<HttpRequest> requestSupplier = () -> {
            String json = toJson(body);
            HttpRequest.Builder builder = baseRequest(path)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json));
            addAuthorization(builder, authenticated);
            return builder.build();
        };
        return sendWithRetry(requestSupplier, responseType, authenticated);
    }

    private <T> CompletableFuture<ApiResponse<T>> sendWithRetry(
            Supplier<HttpRequest> requestSupplier,
            TypeReference<ApiResponse<T>> responseType,
            boolean authenticated
    ) {
        HttpRequest request = requestSupplier.get();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseResponse(response, responseType))
                .handle((result, throwable) -> {
                    if (throwable != null) {
                        Throwable cause = throwable.getCause();
                        if (cause instanceof ApiException apiException && apiException.getStatusCode() == 401 && authenticated) {
                            return refreshAndRetry(requestSupplier, responseType);
                        }
                        if (throwable instanceof RuntimeException re) {
                            throw re;
                        }
                        throw new RuntimeException(throwable);
                    }
                    return CompletableFuture.completedFuture(result);
                })
                .thenCompose(future -> future);
    }

    private <T> CompletableFuture<ApiResponse<T>> refreshAndRetry(
            Supplier<HttpRequest> requestSupplier,
            TypeReference<ApiResponse<T>> responseType
    ) {
        String refreshToken = sessionManager.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            sessionManager.clear();
            return CompletableFuture.failedFuture(new ApiException(401, errorResponse("No refresh token available")));
        }

        TokenRefreshRequest refreshRequest = new TokenRefreshRequest(refreshToken);
        String json = toJson(refreshRequest);
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/auth/refresh"))
                .header("Accept", CONTENT_TYPE)
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    int status = response.statusCode();
                    if (status >= 200 && status < 300) {
                        try {
                            ApiResponse<LoginResponse> refreshRes = objectMapper.readValue(
                                    response.body(), new TypeReference<ApiResponse<LoginResponse>>() {}
                            );
                            if (refreshRes.isSuccess() && refreshRes.getData() != null) {
                                sessionManager.startSession(refreshRes.getData());
                                return refreshRes.getData();
                            }
                        } catch (IOException e) {
                            throw new ApiException("Failed to parse refresh response", e);
                        }
                    }
                    throw new ApiException(status, errorResponse("Token refresh failed"));
                })
                .thenCompose(loginResponse -> {
                    HttpRequest newRequest = requestSupplier.get();
                    return httpClient.sendAsync(newRequest, HttpResponse.BodyHandlers.ofString())
                            .thenApply(response -> parseResponse(response, responseType));
                })
                .exceptionallyCompose(ex -> {
                    sessionManager.clear();
                    if (ex instanceof ApiException) {
                        return CompletableFuture.failedFuture(ex);
                    }
                    if (ex.getCause() instanceof ApiException) {
                        return CompletableFuture.failedFuture(ex.getCause());
                    }
                    return CompletableFuture.failedFuture(new ApiException("Refresh failed", ex));
                });
    }

    private ApiResponse<Void> errorResponse(String message) {
        ApiResponse<Void> res = new ApiResponse<>();
        res.setSuccess(false);
        res.setMessage(message);
        return res;
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
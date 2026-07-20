package com.smartclinic.desktop.service;

import com.smartclinic.desktop.api.PatientApiClient;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.PageResponse;
import com.smartclinic.desktop.dto.PatientResponse;
import java.util.concurrent.CompletableFuture;

public class PatientDesktopService {

    public static final int DEFAULT_PAGE_SIZE = 10;

    private final PatientApiClient patientApiClient;

    public PatientDesktopService(PatientApiClient patientApiClient) {
        this.patientApiClient = patientApiClient;
    }

    public CompletableFuture<PageResponse<PatientResponse>> search(String keyword, int page) {
        return search(keyword, page, DEFAULT_PAGE_SIZE);
    }

    public CompletableFuture<PageResponse<PatientResponse>> search(String keyword, int page, int size) {
        return patientApiClient.search(keyword, page, size)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<PatientResponse> getById(Long id) {
        return patientApiClient.getById(id)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<PatientResponse> create(Object request) {
        return patientApiClient.create(request)
                .thenApply(ApiResponse::getData);
    }

    public CompletableFuture<PatientResponse> update(Long id, Object request) {
        return patientApiClient.update(id, request)
                .thenApply(ApiResponse::getData);
    }
}

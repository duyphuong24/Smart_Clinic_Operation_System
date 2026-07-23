package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.InvoiceResponse;
import com.smartclinic.desktop.dto.PayOSPaymentResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class InvoiceApiClient {

    private final ApiClient apiClient;

    public InvoiceApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<List<InvoiceResponse>>> findAll(String status) {
        String path = "/invoices";
        if (status != null && !status.isBlank()) {
            path += "?status=" + status;
        }
        return apiClient.get(
                path,
                new TypeReference<ApiResponse<List<InvoiceResponse>>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<InvoiceResponse>> getById(Long id) {
        return apiClient.get(
                "/invoices/" + id,
                new TypeReference<ApiResponse<InvoiceResponse>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<InvoiceResponse>> cancel(Long id, String reason) {
        Map<String, String> body = Map.of("reason", reason == null ? "" : reason);
        return apiClient.patch(
                "/invoices/" + id + "/cancel",
                body,
                new TypeReference<ApiResponse<InvoiceResponse>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<PayOSPaymentResponse>> createPayOSLink(Long invoiceId) {
        return apiClient.post(
                "/invoices/" + invoiceId + "/payos-link",
                null,
                new TypeReference<ApiResponse<PayOSPaymentResponse>>() {},
                true
        );
    }
}

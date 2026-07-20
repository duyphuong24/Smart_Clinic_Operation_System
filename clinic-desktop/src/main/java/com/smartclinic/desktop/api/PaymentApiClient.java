package com.smartclinic.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartclinic.desktop.dto.ApiResponse;
import com.smartclinic.desktop.dto.PaymentRequest;
import com.smartclinic.desktop.dto.PaymentResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PaymentApiClient {

    private final ApiClient apiClient;

    public PaymentApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<ApiResponse<PaymentResponse>> record(Long invoiceId, PaymentRequest request) {
        return apiClient.post(
                "/invoices/" + invoiceId + "/payments",
                request,
                new TypeReference<ApiResponse<PaymentResponse>>() {},
                true
        );
    }

    public CompletableFuture<ApiResponse<List<PaymentResponse>>> findAll(Long invoiceId, String status) {
        StringBuilder path = new StringBuilder("/payments?");
        if (invoiceId != null) {
            path.append("invoiceId=").append(invoiceId).append("&");
        }
        if (status != null && !status.isBlank()) {
            path.append("status=").append(status);
        }
        return apiClient.get(
                path.toString(),
                new TypeReference<ApiResponse<List<PaymentResponse>>>() {},
                true
        );
    }
}

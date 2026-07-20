package com.smartclinic.desktop.service;

import com.smartclinic.desktop.api.ApiException;
import com.smartclinic.desktop.api.PaymentApiClient;
import com.smartclinic.desktop.dto.PaymentRequest;
import com.smartclinic.desktop.dto.PaymentResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PaymentDesktopService {

    private final PaymentApiClient paymentApiClient;

    public PaymentDesktopService(PaymentApiClient paymentApiClient) {
        this.paymentApiClient = paymentApiClient;
    }

    public CompletableFuture<PaymentResponse> recordPayment(Long invoiceId, PaymentRequest request) {
        return paymentApiClient.record(invoiceId, request)
                .thenApply(res -> {
                    if (res != null && res.isSuccess()) {
                        return res.getData();
                    }
                    throw new ApiException(res == null ? "No response" : res.getMessage(), null);
                });
    }

    public CompletableFuture<List<PaymentResponse>> findAll(Long invoiceId, String status) {
        return paymentApiClient.findAll(invoiceId, status)
                .thenApply(res -> {
                    if (res != null && res.isSuccess()) {
                        return res.getData() == null ? List.of() : res.getData();
                    }
                    throw new ApiException(res == null ? "No response" : res.getMessage(), null);
                });
    }
}

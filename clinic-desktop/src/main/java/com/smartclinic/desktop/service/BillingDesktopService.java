package com.smartclinic.desktop.service;

import com.smartclinic.desktop.api.ApiException;
import com.smartclinic.desktop.api.InvoiceApiClient;
import com.smartclinic.desktop.dto.InvoiceResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BillingDesktopService {

    private final InvoiceApiClient invoiceApiClient;

    public BillingDesktopService(InvoiceApiClient invoiceApiClient) {
        this.invoiceApiClient = invoiceApiClient;
    }

    public CompletableFuture<List<InvoiceResponse>> findAll(String status) {
        return invoiceApiClient.findAll(status)
                .thenApply(res -> {
                    if (res != null && res.isSuccess()) {
                        return res.getData() == null ? List.of() : res.getData();
                    }
                    throw new ApiException(res == null ? "No response" : res.getMessage(), null);
                });
    }

    public CompletableFuture<InvoiceResponse> getById(Long id) {
        return invoiceApiClient.getById(id)
                .thenApply(res -> {
                    if (res != null && res.isSuccess()) {
                        return res.getData();
                    }
                    throw new ApiException(res == null ? "No response" : res.getMessage(), null);
                });
    }

    public CompletableFuture<InvoiceResponse> cancel(Long id, String reason) {
        return invoiceApiClient.cancel(id, reason)
                .thenApply(res -> {
                    if (res != null && res.isSuccess()) {
                        return res.getData();
                    }
                    throw new ApiException(res == null ? "No response" : res.getMessage(), null);
                });
    }
}

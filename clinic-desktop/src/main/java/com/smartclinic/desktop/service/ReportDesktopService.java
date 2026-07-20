package com.smartclinic.desktop.service;

import com.smartclinic.desktop.api.ApiException;
import com.smartclinic.desktop.api.ReportApiClient;
import com.smartclinic.desktop.dto.DashboardMetricsResponse;
import java.util.concurrent.CompletableFuture;

public class ReportDesktopService {

    private final ReportApiClient reportApiClient;

    public ReportDesktopService(ReportApiClient reportApiClient) {
        this.reportApiClient = reportApiClient;
    }

    public CompletableFuture<DashboardMetricsResponse> getDashboardMetrics() {
        return reportApiClient.getDashboardMetrics()
                .thenApply(res -> {
                    if (res != null && res.isSuccess()) {
                        return res.getData();
                    }
                    throw new ApiException(res == null ? "No response" : res.getMessage(), null);
                });
    }
}

package com.smartclinic.report.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardMetricsResponse {

    private final long todayAppointments;
    private final long activeQueueItems;
    private final long completedVisits;
    private final BigDecimal todayRevenue;
}
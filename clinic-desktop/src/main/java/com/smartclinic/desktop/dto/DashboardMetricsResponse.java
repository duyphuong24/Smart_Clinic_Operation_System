package com.smartclinic.desktop.dto;

import java.math.BigDecimal;

public class DashboardMetricsResponse {

    private long todayAppointments;
    private long activeQueueItems;
    private long completedVisits;
    private BigDecimal todayRevenue;

    public long getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(long todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public long getActiveQueueItems() {
        return activeQueueItems;
    }

    public void setActiveQueueItems(long activeQueueItems) {
        this.activeQueueItems = activeQueueItems;
    }

    public long getCompletedVisits() {
        return completedVisits;
    }

    public void setCompletedVisits(long completedVisits) {
        this.completedVisits = completedVisits;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }
}

package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.navigation.NavigationAware;
import com.smartclinic.desktop.service.ReportDesktopService;
import com.smartclinic.desktop.util.AlertUtil;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class FinancialReportsController implements NavigationAware {

    private final ReportDesktopService reportService;

    @FXML
    private Label revenueValueLabel;

    @FXML
    private Label completedVisitsLabel;

    @FXML
    private Label todayApptsLabel;

    @FXML
    private Label activeQueueLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    public FinancialReportsController(ReportDesktopService reportService) {
        this.reportService = reportService;
    }

    @FXML
    private void initialize() {
        loadMetrics();
    }

    @Override
    public void onNavigate() {
        loadMetrics();
    }

    @FXML
    private void onRefresh() {
        loadMetrics();
    }

    private void loadMetrics() {
        setLoading(true);
        reportService.getDashboardMetrics()
                .whenComplete((metrics, throwable) -> Platform.runLater(() -> {
                    setLoading(false);
                    if (throwable != null) {
                        revenueValueLabel.setText("0.00");
                        completedVisitsLabel.setText("0");
                        todayApptsLabel.setText("0");
                        activeQueueLabel.setText("0");
                        AlertUtil.showError("Load Financial Metrics", throwable);
                        return;
                    }

                    if (metrics != null) {
                        BigDecimal rev = metrics.getTodayRevenue() == null ? BigDecimal.ZERO : metrics.getTodayRevenue();
                        revenueValueLabel.setText(NumberFormat.getCurrencyInstance(Locale.US).format(rev));
                        completedVisitsLabel.setText(String.valueOf(metrics.getCompletedVisits()));
                        todayApptsLabel.setText(String.valueOf(metrics.getTodayAppointments()));
                        activeQueueLabel.setText(String.valueOf(metrics.getActiveQueueItems()));
                    }
                }));
    }

    private void setLoading(boolean loading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(loading);
            loadingIndicator.setManaged(loading);
        }
    }
}

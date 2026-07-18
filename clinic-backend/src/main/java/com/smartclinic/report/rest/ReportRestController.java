package com.smartclinic.report.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.report.dto.DashboardMetricsResponse;
import com.smartclinic.report.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportRestController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardMetricsResponse> dashboardMetrics(HttpServletRequest request) {
        return ApiResponse.success("Dashboard metrics loaded", reportService.dashboardMetrics(), request.getRequestURI());
    }
}
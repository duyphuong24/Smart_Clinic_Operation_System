package com.smartclinic.report.controller;

import com.smartclinic.report.dto.DashboardMetricsResponse;
import com.smartclinic.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public String dashboard(Model model) {
        DashboardMetricsResponse metrics = reportService.dashboardMetrics();
        model.addAttribute("metrics", metrics);
        model.addAttribute("title", "Reports & Analytics");
        return "report/dashboard";
    }
}

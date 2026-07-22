package com.smartclinic.auth.controller;

import com.smartclinic.report.dto.DashboardMetricsResponse;
import com.smartclinic.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final ReportService reportService;

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardMetricsResponse metrics = reportService.dashboardMetrics();

        model.addAttribute("appointmentsCount", metrics.getTodayAppointments());
        model.addAttribute("waitingQueueCount", metrics.getActiveQueueItems());
        model.addAttribute("completedVisitsCount", metrics.getCompletedVisits());
        model.addAttribute("totalRevenue", metrics.getTodayRevenue() != null ? metrics.getTodayRevenue() : BigDecimal.ZERO);
        model.addAttribute("title", "Dashboard");

        return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}


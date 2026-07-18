package com.smartclinic.common.rest;

import com.smartclinic.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthRestController {

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health(HttpServletRequest request) {
        return ApiResponse.success(
                "Smart Clinic API is running",
                Map.of("status", "UP", "service", "clinic-backend"),
                request.getRequestURI()
        );
    }
}
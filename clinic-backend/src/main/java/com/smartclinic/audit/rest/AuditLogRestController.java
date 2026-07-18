package com.smartclinic.audit.rest;

import com.smartclinic.audit.dto.AuditLogResponse;
import com.smartclinic.audit.service.AuditLogService;
import com.smartclinic.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogRestController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ApiResponse<List<AuditLogResponse>> findRecent(HttpServletRequest request) {
        return ApiResponse.success("Audit logs loaded", auditLogService.findRecent(), request.getRequestURI());
    }
}
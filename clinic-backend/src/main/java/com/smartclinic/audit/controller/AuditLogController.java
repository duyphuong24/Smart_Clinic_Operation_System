package com.smartclinic.audit.controller;

import com.smartclinic.audit.dto.AuditLogResponse;
import com.smartclinic.audit.service.AuditLogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public String list(Model model) {
        List<AuditLogResponse> logs = auditLogService.findRecent();
        model.addAttribute("logs", logs);
        model.addAttribute("title", "Audit Logs");
        return "admin/audit-log";
    }
}

package com.smartclinic.audit.service;

import com.smartclinic.audit.dto.AuditLogResponse;
import com.smartclinic.audit.entity.AuditLog;
import com.smartclinic.audit.mapper.AuditLogMapper;
import com.smartclinic.audit.repository.AuditLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String action, String entityType, Long entityId, String message) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActorUserName(currentUserName());
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setMessage(message);
        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> findRecent() {
        return auditLogRepository.findTop100ByOrderByCreatedAtDesc().stream()
                .map(AuditLogMapper::toResponse)
                .toList();
    }

    private String currentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        return authentication.getName();
    }
}
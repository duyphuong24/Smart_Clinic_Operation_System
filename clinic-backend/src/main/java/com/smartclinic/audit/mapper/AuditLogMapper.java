package com.smartclinic.audit.mapper;

import com.smartclinic.audit.dto.AuditLogResponse;
import com.smartclinic.audit.entity.AuditLog;

public final class AuditLogMapper {

    private AuditLogMapper() {
    }

    public static AuditLogResponse toResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .actorUserName(auditLog.getActorUserName())
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .message(auditLog.getMessage())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
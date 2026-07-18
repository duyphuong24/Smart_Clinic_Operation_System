package com.smartclinic.audit.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditLogResponse {

    private final Long id;
    private final String actorUserName;
    private final String action;
    private final String entityType;
    private final Long entityId;
    private final String message;
    private final LocalDateTime createdAt;
}
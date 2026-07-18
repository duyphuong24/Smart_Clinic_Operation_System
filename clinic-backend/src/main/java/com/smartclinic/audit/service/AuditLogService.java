package com.smartclinic.audit.service;

import com.smartclinic.audit.dto.AuditLogResponse;
import java.util.List;

public interface AuditLogService {

    void record(String action, String entityType, Long entityId, String message);

    List<AuditLogResponse> findRecent();
}
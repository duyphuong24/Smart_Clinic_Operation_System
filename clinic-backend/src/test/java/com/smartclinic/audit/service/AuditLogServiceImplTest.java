package com.smartclinic.audit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartclinic.audit.entity.AuditLog;
import com.smartclinic.audit.repository.AuditLogRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl service;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recordShouldUseAuthenticatedUserName() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("doctor", null, List.of())
        );

        service.record("ENCOUNTER_COMPLETED", "Encounter", 1L, "Encounter completed");

        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void findRecentShouldMapAuditLogs() {
        AuditLog log = new AuditLog();
        log.setId(1L);
        log.setActorUserName("system");
        log.setAction("PAYMENT_RECORDED");
        log.setEntityType("Payment");
        log.setEntityId(9L);
        log.setMessage("Payment recorded");
        when(auditLogRepository.findTop100ByOrderByCreatedAtDesc()).thenReturn(List.of(log));

        var response = service.findRecent();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getAction()).isEqualTo("PAYMENT_RECORDED");
    }
}
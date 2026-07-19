package com.smartclinic.desktop.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smartclinic.desktop.dto.QueueItemResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class QueueUiUtilTest {

    @Test
    void formatPatientShouldIncludeCodeWhenAvailable() {
        QueueItemResponse item = new QueueItemResponse();
        item.setPatientName("Nguyen Van A");
        item.setPatientCode("PT-00001");

        assertEquals("Nguyen Van A (PT-00001)", QueueUiUtil.formatPatient(item));
    }

    @Test
    void actionPermissionsShouldFollowRoleAndStatus() {
        List<String> receptionist = List.of("ROLE_RECEPTIONIST");
        List<String> doctor = List.of("ROLE_DOCTOR");

        assertTrue(QueueUiUtil.canCall(receptionist, "WAITING"));
        assertFalse(QueueUiUtil.canStart(receptionist, "CALLED"));
        assertTrue(QueueUiUtil.canSkip(receptionist, "WAITING"));

        assertTrue(QueueUiUtil.canStart(doctor, "CALLED"));
        assertTrue(QueueUiUtil.canDone(doctor, "IN_SERVICE"));
        assertFalse(QueueUiUtil.canCall(doctor, "IN_SERVICE"));
    }

    @Test
    void statusStyleClassShouldMapKnownStatuses() {
        assertEquals("queue-status-waiting", QueueUiUtil.statusStyleClass("WAITING"));
        assertEquals("queue-status-in-service", QueueUiUtil.statusStyleClass("IN_SERVICE"));
    }
}

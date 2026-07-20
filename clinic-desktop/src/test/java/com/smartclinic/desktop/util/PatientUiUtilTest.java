package com.smartclinic.desktop.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smartclinic.desktop.dto.PatientResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PatientUiUtilTest {

    @Test
    void formatDateOfBirthShouldUseDisplayPattern() {
        assertEquals("18-07-2000", PatientUiUtil.formatDateOfBirth(LocalDate.of(2000, 7, 18)));
        assertEquals("-", PatientUiUtil.formatDateOfBirth(null));
    }

    @Test
    void statusStyleClassShouldMapActiveAndArchived() {
        assertEquals("patient-status-active", PatientUiUtil.statusStyleClass("ACTIVE"));
        assertEquals("patient-status-archived", PatientUiUtil.statusStyleClass("ARCHIVED"));
    }

    @Test
    void activePatientShouldBeDetected() {
        PatientResponse active = new PatientResponse();
        active.setStatus("ACTIVE");
        PatientResponse archived = new PatientResponse();
        archived.setStatus("ARCHIVED");

        assertTrue(active.isActive());
        assertTrue(!archived.isActive());
    }
}

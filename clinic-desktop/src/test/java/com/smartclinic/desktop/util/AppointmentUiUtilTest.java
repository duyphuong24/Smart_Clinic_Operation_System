package com.smartclinic.desktop.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smartclinic.desktop.dto.AppointmentResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class AppointmentUiUtilTest {

    @Test
    void filterShouldMatchStatusAndKeyword() {
        AppointmentResponse booked = appointment("APT-001", "Nguyen Van A", "Dr. Lee", "BOOKED");
        AppointmentResponse checkedIn = appointment("APT-002", "Tran Thi B", "Dr. Lee", "CHECKED_IN");

        List<AppointmentResponse> filtered = AppointmentUiUtil.filter(
                List.of(booked, checkedIn),
                "BOOKED",
                "nguyen"
        );

        assertEquals(1, filtered.size());
        assertEquals("APT-001", filtered.getFirst().getAppointmentCode());
    }

    @Test
    void isCheckInAllowedShouldFollowBusinessRule() {
        AppointmentResponse booked = appointment("APT-003", "A", "Dr. X", "BOOKED");
        AppointmentResponse cancelled = appointment("APT-004", "B", "Dr. X", "CANCELLED");

        assertTrue(booked.isCheckInAllowed());
        assertTrue(!cancelled.isCheckInAllowed());
    }

    private AppointmentResponse appointment(String code, String patient, String doctor, String status) {
        AppointmentResponse response = new AppointmentResponse();
        response.setAppointmentCode(code);
        response.setPatientName(patient);
        response.setDoctorName(doctor);
        response.setStatus(status);
        return response;
    }
}

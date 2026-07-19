package com.smartclinic.desktop.dto;

public class AppointmentCheckInRequest {

    private Long appointmentId;

    public AppointmentCheckInRequest() {
    }

    public AppointmentCheckInRequest(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }
}

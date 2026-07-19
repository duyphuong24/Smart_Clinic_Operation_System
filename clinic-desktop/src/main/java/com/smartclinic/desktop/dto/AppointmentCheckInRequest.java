package com.smartclinic.desktop.dto;

public class AppointmentCheckInRequest {

    private Long appointmentId;
    private String priority;

    public AppointmentCheckInRequest() {
    }

    public AppointmentCheckInRequest(Long appointmentId) {
        this.appointmentId = appointmentId;
        this.priority = "NORMAL";
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}

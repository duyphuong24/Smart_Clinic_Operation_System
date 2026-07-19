package com.smartclinic.desktop.dto;

public class DoctorResponse {

    private Long id;
    private String fullName;
    private String specialtyName;
    private Long defaultRoomId;
    private String defaultRoomCode;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialtyName() {
        return specialtyName;
    }

    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName;
    }

    public Long getDefaultRoomId() {
        return defaultRoomId;
    }

    public void setDefaultRoomId(Long defaultRoomId) {
        this.defaultRoomId = defaultRoomId;
    }

    public String getDefaultRoomCode() {
        return defaultRoomCode;
    }

    public void setDefaultRoomCode(String defaultRoomCode) {
        this.defaultRoomCode = defaultRoomCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String displayLabel() {
        if (specialtyName == null || specialtyName.isBlank()) {
            return fullName;
        }
        return fullName + " (" + specialtyName + ")";
    }
}

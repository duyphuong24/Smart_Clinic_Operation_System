package com.smartclinic.desktop.dto;

public class RoomResponse {

    private Long id;
    private String roomCode;
    private String name;
    private String floor;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String displayLabel() {
        if (roomCode == null || roomCode.isBlank()) {
            return name == null ? "-" : name;
        }
        return name + " (" + roomCode + ")";
    }
}

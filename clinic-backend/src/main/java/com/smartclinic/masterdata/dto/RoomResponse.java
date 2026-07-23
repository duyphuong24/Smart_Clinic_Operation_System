package com.smartclinic.masterdata.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResponse {

    private final Long id;
    private final String roomCode;
    private final String name;
    private final String floor;
    private final boolean active;
}
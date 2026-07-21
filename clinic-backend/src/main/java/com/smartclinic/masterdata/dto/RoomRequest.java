package com.smartclinic.masterdata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {

    @NotBlank
    private String roomCode;

    @NotBlank
    private String name;

    private String floor;

    private boolean active = true;
}
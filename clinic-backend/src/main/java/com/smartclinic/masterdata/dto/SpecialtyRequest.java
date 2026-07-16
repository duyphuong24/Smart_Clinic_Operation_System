package com.smartclinic.masterdata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialtyRequest {

    @NotBlank
    private String name;

    private String description;

    private boolean active = true;
}
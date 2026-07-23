package com.smartclinic.masterdata.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpecialtyResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final boolean active;
}
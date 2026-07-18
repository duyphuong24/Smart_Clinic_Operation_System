package com.smartclinic.encounter.serviceorder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncounterServiceOrderRequest {

    @NotNull
    private Long serviceCatalogId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @Size(max = 1000)
    private String note;
}
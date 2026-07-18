package com.smartclinic.encounter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncounterCreateRequest {

    @NotNull
    private Long visitId;

    private String chiefComplaint;

    private String diagnosis;

    private String clinicalNote;
}
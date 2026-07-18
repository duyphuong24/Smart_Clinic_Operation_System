package com.smartclinic.encounter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncounterUpdateRequest {

    private String chiefComplaint;

    private String diagnosis;

    private String clinicalNote;
}
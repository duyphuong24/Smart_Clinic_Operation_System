package com.smartclinic.encounter.service;

import com.smartclinic.encounter.dto.EncounterCreateRequest;
import com.smartclinic.encounter.dto.EncounterResponse;
import com.smartclinic.encounter.dto.EncounterUpdateRequest;

public interface EncounterWorkflowService {

    EncounterResponse getById(Long id);

    EncounterResponse create(EncounterCreateRequest request);

    EncounterResponse update(Long id, EncounterUpdateRequest request);

    EncounterResponse complete(Long id);
}
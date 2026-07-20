package com.smartclinic.encounter.service;

import com.smartclinic.encounter.dto.EncounterCreateRequest;
import com.smartclinic.encounter.dto.EncounterResponse;
import com.smartclinic.encounter.dto.EncounterUpdateRequest;

public interface EncounterWorkflowService {

    EncounterResponse getById(Long id);

    EncounterResponse create(EncounterCreateRequest request);

    @org.springframework.security.access.prepost.PreAuthorize("@securityHelper.isEncounterOwner(#id)")
    EncounterResponse update(Long id, EncounterUpdateRequest request);

    @org.springframework.security.access.prepost.PreAuthorize("@securityHelper.isEncounterOwner(#id)")
    EncounterResponse complete(Long id);
}
package com.smartclinic.encounter.serviceorder.service;

import com.smartclinic.encounter.serviceorder.dto.EncounterServiceOrderRequest;
import com.smartclinic.encounter.serviceorder.dto.EncounterServiceOrderResponse;
import java.util.List;

public interface EncounterServiceOrderService {

    EncounterServiceOrderResponse add(Long encounterId, EncounterServiceOrderRequest request);

    List<EncounterServiceOrderResponse> findByEncounter(Long encounterId);

    EncounterServiceOrderResponse complete(Long id);

    EncounterServiceOrderResponse cancel(Long id);
}
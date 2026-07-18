package com.smartclinic.encounter.serviceorder.mapper;

import com.smartclinic.encounter.serviceorder.dto.EncounterServiceOrderResponse;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrder;

public final class EncounterServiceOrderMapper {

    private EncounterServiceOrderMapper() {
    }

    public static EncounterServiceOrderResponse toResponse(EncounterServiceOrder order) {
        return EncounterServiceOrderResponse.builder()
                .id(order.getId())
                .encounterId(order.getEncounter().getId())
                .serviceCatalogId(order.getServiceCatalog().getId())
                .serviceCode(order.getServiceCatalog().getServiceCode())
                .serviceName(order.getServiceCatalog().getName())
                .serviceType(order.getServiceCatalog().getType())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .note(order.getNote())
                .build();
    }
}
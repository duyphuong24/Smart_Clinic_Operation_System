package com.smartclinic.encounter.serviceorder.dto;

import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrderStatus;
import com.smartclinic.servicecatalog.entity.ServiceType;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EncounterServiceOrderResponse {

    private final Long id;
    private final Long encounterId;
    private final Long serviceCatalogId;
    private final String serviceCode;
    private final String serviceName;
    private final ServiceType serviceType;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalAmount;
    private final EncounterServiceOrderStatus status;
    private final String note;
}
package com.smartclinic.servicecatalog.dto;

import com.smartclinic.servicecatalog.entity.ServiceType;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceCatalogResponse {

    private final Long id;
    private final String serviceCode;
    private final String name;
    private final String description;
    private final ServiceType type;
    private final BigDecimal price;
    private final boolean active;
}
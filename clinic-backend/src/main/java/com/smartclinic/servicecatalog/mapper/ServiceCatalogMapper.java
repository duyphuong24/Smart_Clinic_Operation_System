package com.smartclinic.servicecatalog.mapper;

import com.smartclinic.servicecatalog.dto.ServiceCatalogResponse;
import com.smartclinic.servicecatalog.entity.ServiceCatalog;

public final class ServiceCatalogMapper {

    private ServiceCatalogMapper() {
    }

    public static ServiceCatalogResponse toResponse(ServiceCatalog service) {
        return ServiceCatalogResponse.builder()
                .id(service.getId())
                .serviceCode(service.getServiceCode())
                .name(service.getName())
                .type(service.getType())
                .price(service.getPrice())
                .active(service.isActive())
                .build();
    }
}
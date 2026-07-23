package com.smartclinic.servicecatalog.service;

import com.smartclinic.servicecatalog.dto.ServiceCatalogRequest;
import com.smartclinic.servicecatalog.dto.ServiceCatalogResponse;
import com.smartclinic.servicecatalog.entity.ServiceType;
import java.util.List;

public interface ServiceCatalogService {

    List<ServiceCatalogResponse> findAll(ServiceType type, Boolean activeOnly);

    ServiceCatalogResponse getById(Long id);

    ServiceCatalogResponse create(ServiceCatalogRequest request);

    ServiceCatalogResponse update(Long id, ServiceCatalogRequest request);

    void deactivate(Long id);

    ServiceCatalogResponse toggleStatus(Long id);
}
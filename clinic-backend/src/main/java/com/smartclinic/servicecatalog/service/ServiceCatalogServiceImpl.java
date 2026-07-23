package com.smartclinic.servicecatalog.service;

import com.smartclinic.audit.service.AuditLogService;
import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.servicecatalog.dto.ServiceCatalogRequest;
import com.smartclinic.servicecatalog.dto.ServiceCatalogResponse;
import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.entity.ServiceType;
import com.smartclinic.servicecatalog.mapper.ServiceCatalogMapper;
import com.smartclinic.servicecatalog.repository.ServiceCatalogRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCatalogResponse> findAll(ServiceType type, Boolean activeOnly) {
        boolean onlyActive = Boolean.TRUE.equals(activeOnly);
        List<ServiceCatalog> services;
        if (type != null && onlyActive) {
            services = serviceCatalogRepository.findByTypeAndActiveTrue(type);
        } else if (type != null) {
            services = serviceCatalogRepository.findByType(type);
        } else if (onlyActive) {
            services = serviceCatalogRepository.findByActiveTrue();
        } else {
            services = serviceCatalogRepository.findAll();
        }
        return services.stream()
                .map(ServiceCatalogMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceCatalogResponse getById(Long id) {
        return ServiceCatalogMapper.toResponse(findService(id));
    }

    @Override
    public ServiceCatalogResponse create(ServiceCatalogRequest request) {
        validatePrice(request.getPrice());
        String code = normalizeCode(request.getServiceCode());
        if (serviceCatalogRepository.existsByServiceCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Service code already exists");
        }
        ServiceCatalog service = new ServiceCatalog();
        service.setServiceCode(code);
        service.setName(request.getName().trim());
        service.setType(request.getType());
        service.setPrice(request.getPrice());
        service.setActive(request.isActive());
        
        ServiceCatalog saved = serviceCatalogRepository.save(service);
        auditLogService.record("CREATE_SERVICE", "SERVICE_CATALOG", saved.getId(), "Created medical service: " + saved.getName() + " (" + saved.getServiceCode() + ")");
        return ServiceCatalogMapper.toResponse(saved);
    }

    @Override
    public ServiceCatalogResponse update(Long id, ServiceCatalogRequest request) {
        validatePrice(request.getPrice());
        ServiceCatalog service = findService(id);
        String code = normalizeCode(request.getServiceCode());
        if (!service.getServiceCode().equalsIgnoreCase(code)
                && serviceCatalogRepository.existsByServiceCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Service code already exists");
        }
        service.setServiceCode(code);
        service.setName(request.getName().trim());
        service.setType(request.getType());
        service.setPrice(request.getPrice());
        service.setActive(request.isActive());
        
        ServiceCatalog saved = serviceCatalogRepository.save(service);
        auditLogService.record("UPDATE_SERVICE", "SERVICE_CATALOG", saved.getId(), "Updated medical service: " + saved.getName());
        return ServiceCatalogMapper.toResponse(saved);
    }

    @Override
    public void deactivate(Long id) {
        ServiceCatalog service = findService(id);
        service.setActive(false);
        serviceCatalogRepository.save(service);
        auditLogService.record("DEACTIVATE_SERVICE", "SERVICE_CATALOG", service.getId(), "Deactivated service: " + service.getName());
    }

    @Override
    public ServiceCatalogResponse toggleStatus(Long id) {
        ServiceCatalog service = findService(id);
        service.setActive(!service.isActive());
        ServiceCatalog saved = serviceCatalogRepository.save(service);
        auditLogService.record("TOGGLE_SERVICE_STATUS", "SERVICE_CATALOG", saved.getId(), "Toggled service active status to " + saved.isActive() + " for " + saved.getName());
        return ServiceCatalogMapper.toResponse(saved);
    }

    private ServiceCatalog findService(Long id) {
        return serviceCatalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private void validatePrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Service price must not be negative");
        }
    }
}
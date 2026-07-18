package com.smartclinic.servicecatalog.service;

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
        return ServiceCatalogMapper.toResponse(serviceCatalogRepository.save(service));
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
        return ServiceCatalogMapper.toResponse(serviceCatalogRepository.save(service));
    }

    @Override
    public void deactivate(Long id) {
        ServiceCatalog service = findService(id);
        service.setActive(false);
        serviceCatalogRepository.save(service);
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
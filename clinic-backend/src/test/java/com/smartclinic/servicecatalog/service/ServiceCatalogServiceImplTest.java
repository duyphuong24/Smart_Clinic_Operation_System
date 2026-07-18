package com.smartclinic.servicecatalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.servicecatalog.dto.ServiceCatalogRequest;
import com.smartclinic.servicecatalog.dto.ServiceCatalogResponse;
import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.entity.ServiceType;
import com.smartclinic.servicecatalog.repository.ServiceCatalogRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceCatalogServiceImplTest {

    @Mock
    private ServiceCatalogRepository serviceCatalogRepository;

    @InjectMocks
    private ServiceCatalogServiceImpl service;

    @Test
    void createShouldRejectDuplicateServiceCode() {
        when(serviceCatalogRepository.existsByServiceCodeIgnoreCase("LAB-001")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request(" lab-001 ", BigDecimal.valueOf(120000))))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Service code already exists");
    }

    @Test
    void createShouldRejectNegativePrice() {
        assertThatThrownBy(() -> service.create(request("LAB-001", BigDecimal.valueOf(-1))))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Service price must not be negative");
    }

    @Test
    void createShouldNormalizeCodeAndTrimName() {
        when(serviceCatalogRepository.existsByServiceCodeIgnoreCase("LAB-001")).thenReturn(false);
        when(serviceCatalogRepository.save(any(ServiceCatalog.class))).thenAnswer(invocation -> {
            ServiceCatalog catalog = invocation.getArgument(0);
            catalog.setId(1L);
            return catalog;
        });
        ServiceCatalogRequest request = request(" lab-001 ", BigDecimal.valueOf(120000));
        request.setName(" Blood test ");

        ServiceCatalogResponse response = service.create(request);

        assertThat(response.getServiceCode()).isEqualTo("LAB-001");
        assertThat(response.getName()).isEqualTo("Blood test");
        assertThat(response.getType()).isEqualTo(ServiceType.LAB_TEST);
        assertThat(response.getPrice()).isEqualByComparingTo("120000");
        assertThat(response.isActive()).isTrue();
    }

    @Test
    void findAllShouldFilterByTypeAndActive() {
        ServiceCatalog catalog = serviceCatalog("LAB-001", ServiceType.LAB_TEST, true);
        when(serviceCatalogRepository.findByTypeAndActiveTrue(ServiceType.LAB_TEST)).thenReturn(List.of(catalog));

        List<ServiceCatalogResponse> response = service.findAll(ServiceType.LAB_TEST, true);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getType()).isEqualTo(ServiceType.LAB_TEST);
        verify(serviceCatalogRepository).findByTypeAndActiveTrue(ServiceType.LAB_TEST);
    }

    @Test
    void deactivateShouldSetInactive() {
        ServiceCatalog catalog = serviceCatalog("LAB-001", ServiceType.LAB_TEST, true);
        when(serviceCatalogRepository.findById(1L)).thenReturn(Optional.of(catalog));

        service.deactivate(1L);

        assertThat(catalog.isActive()).isFalse();
        verify(serviceCatalogRepository).save(catalog);
    }

    private ServiceCatalogRequest request(String code, BigDecimal price) {
        ServiceCatalogRequest request = new ServiceCatalogRequest();
        request.setServiceCode(code);
        request.setName("Blood test");
        request.setType(ServiceType.LAB_TEST);
        request.setPrice(price);
        request.setActive(true);
        return request;
    }

    private ServiceCatalog serviceCatalog(String code, ServiceType type, boolean active) {
        ServiceCatalog catalog = new ServiceCatalog();
        catalog.setId(1L);
        catalog.setServiceCode(code);
        catalog.setName("Blood test");
        catalog.setType(type);
        catalog.setPrice(BigDecimal.valueOf(120000));
        catalog.setActive(active);
        return catalog;
    }
}
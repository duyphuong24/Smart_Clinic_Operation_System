package com.smartclinic.encounter.serviceorder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.entity.EncounterStatus;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.encounter.serviceorder.dto.EncounterServiceOrderRequest;
import com.smartclinic.encounter.serviceorder.dto.EncounterServiceOrderResponse;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrder;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrderStatus;
import com.smartclinic.encounter.serviceorder.repository.EncounterServiceOrderRepository;
import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.entity.ServiceType;
import com.smartclinic.servicecatalog.repository.ServiceCatalogRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EncounterServiceOrderServiceImplTest {

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private ServiceCatalogRepository serviceCatalogRepository;

    @Mock
    private EncounterServiceOrderRepository encounterServiceOrderRepository;

    @InjectMocks
    private EncounterServiceOrderServiceImpl service;

    @Test
    void addShouldRejectCompletedEncounter() {
        Encounter encounter = encounter(EncounterStatus.COMPLETED);
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));

        assertThatThrownBy(() -> service.add(1L, request(2L, 1)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Service orders can only be changed while encounter is open");
    }

    @Test
    void addShouldRejectInactiveService() {
        Encounter encounter = encounter(EncounterStatus.OPEN);
        ServiceCatalog catalog = serviceCatalog(false, BigDecimal.valueOf(250000));
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));
        when(serviceCatalogRepository.findById(2L)).thenReturn(Optional.of(catalog));

        assertThatThrownBy(() -> service.add(1L, request(2L, 1)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Inactive service cannot be ordered");
    }

    @Test
    void addShouldRejectInvalidQuantity() {
        Encounter encounter = encounter(EncounterStatus.OPEN);
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));

        assertThatThrownBy(() -> service.add(1L, request(2L, 0)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Service order quantity must be greater than zero");
    }

    @Test
    void addShouldSnapshotUnitPriceAndTotalAmount() {
        Encounter encounter = encounter(EncounterStatus.OPEN);
        ServiceCatalog catalog = serviceCatalog(true, BigDecimal.valueOf(250000));
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter));
        when(serviceCatalogRepository.findById(2L)).thenReturn(Optional.of(catalog));
        when(encounterServiceOrderRepository.save(any(EncounterServiceOrder.class))).thenAnswer(invocation -> {
            EncounterServiceOrder order = invocation.getArgument(0);
            order.setId(9L);
            return order;
        });

        EncounterServiceOrderResponse response = service.add(1L, request(2L, 3));

        assertThat(response.getUnitPrice()).isEqualByComparingTo("250000");
        assertThat(response.getTotalAmount()).isEqualByComparingTo("750000");
        assertThat(response.getStatus()).isEqualTo(EncounterServiceOrderStatus.ORDERED);
        assertThat(response.getNote()).isEqualTo("Need fast result");
    }

    @Test
    void completeShouldRejectNonOrderedOrder() {
        EncounterServiceOrder order = order(EncounterServiceOrderStatus.CANCELLED);
        when(encounterServiceOrderRepository.findById(9L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.complete(9L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only ORDERED service order can change status");
    }

    @Test
    void cancelShouldSetCancelled() {
        EncounterServiceOrder order = order(EncounterServiceOrderStatus.ORDERED);
        when(encounterServiceOrderRepository.findById(9L)).thenReturn(Optional.of(order));
        when(encounterServiceOrderRepository.save(order)).thenReturn(order);

        EncounterServiceOrderResponse response = service.cancel(9L);

        assertThat(response.getStatus()).isEqualTo(EncounterServiceOrderStatus.CANCELLED);
        verify(encounterServiceOrderRepository).save(order);
    }

    private EncounterServiceOrderRequest request(Long serviceCatalogId, Integer quantity) {
        EncounterServiceOrderRequest request = new EncounterServiceOrderRequest();
        request.setServiceCatalogId(serviceCatalogId);
        request.setQuantity(quantity);
        request.setNote(" Need fast result ");
        return request;
    }

    private Encounter encounter(EncounterStatus status) {
        Encounter encounter = new Encounter();
        encounter.setId(1L);
        encounter.setStatus(status);
        return encounter;
    }

    private ServiceCatalog serviceCatalog(boolean active, BigDecimal price) {
        ServiceCatalog catalog = new ServiceCatalog();
        catalog.setId(2L);
        catalog.setServiceCode("LAB-001");
        catalog.setName("Blood test");
        catalog.setType(ServiceType.LAB_TEST);
        catalog.setPrice(price);
        catalog.setActive(active);
        return catalog;
    }

    private EncounterServiceOrder order(EncounterServiceOrderStatus status) {
        EncounterServiceOrder order = new EncounterServiceOrder();
        order.setId(9L);
        order.setEncounter(encounter(EncounterStatus.OPEN));
        order.setServiceCatalog(serviceCatalog(true, BigDecimal.valueOf(250000)));
        order.setQuantity(1);
        order.setUnitPrice(BigDecimal.valueOf(250000));
        order.setTotalAmount(BigDecimal.valueOf(250000));
        order.setStatus(status);
        return order;
    }
}
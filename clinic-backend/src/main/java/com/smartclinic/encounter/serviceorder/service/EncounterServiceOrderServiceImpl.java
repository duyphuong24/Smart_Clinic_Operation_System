package com.smartclinic.encounter.serviceorder.service;

import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.entity.EncounterStatus;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.encounter.serviceorder.dto.EncounterServiceOrderRequest;
import com.smartclinic.encounter.serviceorder.dto.EncounterServiceOrderResponse;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrder;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrderStatus;
import com.smartclinic.encounter.serviceorder.mapper.EncounterServiceOrderMapper;
import com.smartclinic.encounter.serviceorder.repository.EncounterServiceOrderRepository;
import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.repository.ServiceCatalogRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EncounterServiceOrderServiceImpl implements EncounterServiceOrderService {

    private final EncounterRepository encounterRepository;
    private final ServiceCatalogRepository serviceCatalogRepository;
    private final EncounterServiceOrderRepository encounterServiceOrderRepository;

    @Override
    public EncounterServiceOrderResponse add(Long encounterId, EncounterServiceOrderRequest request) {
        Encounter encounter = findEncounter(encounterId);
        assertEncounterOpen(encounter);
        validateQuantity(request.getQuantity());

        ServiceCatalog serviceCatalog = serviceCatalogRepository.findById(request.getServiceCatalogId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        if (!serviceCatalog.isActive()) {
            throw new BadRequestException("Inactive service cannot be ordered");
        }

        EncounterServiceOrder order = new EncounterServiceOrder();
        order.setEncounter(encounter);
        order.setServiceCatalog(serviceCatalog);
        order.setQuantity(request.getQuantity());
        order.setUnitPrice(serviceCatalog.getPrice());
        order.setTotalAmount(serviceCatalog.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setStatus(EncounterServiceOrderStatus.ORDERED);
        order.setNote(trimToNull(request.getNote()));
        return EncounterServiceOrderMapper.toResponse(encounterServiceOrderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EncounterServiceOrderResponse> findByEncounter(Long encounterId) {
        if (!encounterRepository.existsById(encounterId)) {
            throw new ResourceNotFoundException("Encounter not found");
        }
        return encounterServiceOrderRepository.findByEncounterId(encounterId).stream()
                .map(EncounterServiceOrderMapper::toResponse)
                .toList();
    }

    @Override
    public EncounterServiceOrderResponse complete(Long id) {
        EncounterServiceOrder order = findOrder(id);
        assertEncounterOpen(order.getEncounter());
        assertOrdered(order);
        order.setStatus(EncounterServiceOrderStatus.COMPLETED);
        return EncounterServiceOrderMapper.toResponse(encounterServiceOrderRepository.save(order));
    }

    @Override
    public EncounterServiceOrderResponse cancel(Long id) {
        EncounterServiceOrder order = findOrder(id);
        assertEncounterOpen(order.getEncounter());
        assertOrdered(order);
        order.setStatus(EncounterServiceOrderStatus.CANCELLED);
        return EncounterServiceOrderMapper.toResponse(encounterServiceOrderRepository.save(order));
    }

    private Encounter findEncounter(Long id) {
        return encounterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Encounter not found"));
    }

    private EncounterServiceOrder findOrder(Long id) {
        return encounterServiceOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Encounter service order not found"));
    }

    private void assertEncounterOpen(Encounter encounter) {
        if (encounter.getStatus() != EncounterStatus.OPEN) {
            throw new BadRequestException("Service orders can only be changed while encounter is open");
        }
    }

    private void assertOrdered(EncounterServiceOrder order) {
        if (order.getStatus() != EncounterServiceOrderStatus.ORDERED) {
            throw new BadRequestException("Only ORDERED service order can change status");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new BadRequestException("Service order quantity must be greater than zero");
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
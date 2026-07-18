package com.smartclinic.encounter.serviceorder.repository;

import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrder;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncounterServiceOrderRepository extends JpaRepository<EncounterServiceOrder, Long> {

    List<EncounterServiceOrder> findByEncounterId(Long encounterId);

    List<EncounterServiceOrder> findByEncounterIdAndStatusNot(Long encounterId, EncounterServiceOrderStatus status);
}
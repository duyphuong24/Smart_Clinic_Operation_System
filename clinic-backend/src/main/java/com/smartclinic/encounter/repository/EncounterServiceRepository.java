package com.smartclinic.encounter.repository;

import com.smartclinic.encounter.entity.EncounterService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EncounterServiceRepository extends JpaRepository<EncounterService, Long> {
    List<EncounterService> findByEncounterId(Long encounterId);
}

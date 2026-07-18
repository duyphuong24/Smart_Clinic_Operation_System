package com.smartclinic.encounter.repository;

import com.smartclinic.encounter.entity.Encounter;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, Long> {

    Optional<Encounter> findByVisitId(Long visitId);

    boolean existsByVisitId(Long visitId);
}
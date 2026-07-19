package com.smartclinic.masterdata.repository;

import com.smartclinic.masterdata.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    boolean existsByNameIgnoreCase(String name);

    java.util.Optional<Specialty> findByNameIgnoreCase(String name);
}
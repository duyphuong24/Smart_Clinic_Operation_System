package com.smartclinic.patient.repository;

import com.smartclinic.patient.entity.Patient;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByPatientCode(String patientCode);

    Optional<Patient> findTopByOrderByIdDesc();

    @Query("""
            select p from Patient p
            where :keyword is null
               or :keyword = ''
               or lower(p.patientCode) like lower(concat('%', :keyword, '%'))
               or lower(p.fullName) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(p.phone, '')) like lower(concat('%', :keyword, '%'))
            """)
    Page<Patient> search(@Param("keyword") String keyword, Pageable pageable);
}
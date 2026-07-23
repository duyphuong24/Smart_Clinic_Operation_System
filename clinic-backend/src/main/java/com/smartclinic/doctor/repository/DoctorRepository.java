package com.smartclinic.doctor.repository;

import com.smartclinic.doctor.entity.Doctor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByStaffId(Long staffId);

    boolean existsByLicenseNoIgnoreCase(String licenseNo);

    @EntityGraph(attributePaths = {"staff", "staff.user", "specialty", "defaultRoom"})
    Optional<Doctor> findByStaffUserUserName(String userName);

    @Override
    @EntityGraph(attributePaths = {"staff", "staff.user", "specialty", "defaultRoom"})
    List<Doctor> findAll();
}
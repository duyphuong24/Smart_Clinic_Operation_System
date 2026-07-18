package com.smartclinic.doctor.repository;

import com.smartclinic.doctor.entity.Doctor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByStaffId(Long staffId);

    boolean existsByLicenseNoIgnoreCase(String licenseNo);

    Optional<Doctor> findByStaffUserUserName(String userName);
}
package com.smartclinic.staff.repository;

import com.smartclinic.staff.entity.Staff;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByUserId(Long userId);

    Optional<Staff> findByUserId(Long userId);
}
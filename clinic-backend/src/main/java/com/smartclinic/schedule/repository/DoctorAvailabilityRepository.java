package com.smartclinic.schedule.repository;

import com.smartclinic.schedule.entity.DoctorAvailability;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(Long doctorId);
}
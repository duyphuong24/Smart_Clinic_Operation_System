package com.smartclinic.schedule.repository;

import com.smartclinic.schedule.entity.DoctorAvailability;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(Long doctorId);

    @Query("""
            select case when count(a) > 0 then true else false end
            from DoctorAvailability a
            where a.doctor.id = :doctorId
              and a.dayOfWeek = :dayOfWeek
              and a.active = true
              and a.doctor.active = true
              and a.room.active = true
              and a.startTime <= :startTime
              and a.endTime >= :endTime
            """)
    boolean existsActiveAvailabilityCovering(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
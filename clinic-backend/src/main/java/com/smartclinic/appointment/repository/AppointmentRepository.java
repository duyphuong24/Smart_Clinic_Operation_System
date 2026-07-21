package com.smartclinic.appointment.repository;

import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.appointment.entity.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByAppointmentCode(String appointmentCode);

    Optional<Appointment> findTopByOrderByIdDesc();

    List<Appointment> findByScheduledStartBetweenOrderByScheduledStartAsc(LocalDateTime start, LocalDateTime end);

    long countByScheduledStartBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            select case when count(a) > 0 then true else false end
            from Appointment a
            where a.doctor.id = :doctorId
              and a.status in :statuses
              and a.scheduledStart < :end
              and a.scheduledEnd > :start
            """)
    boolean existsActiveOverlap(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );

    @Query("""
            select case when count(a) > 0 then true else false end
            from Appointment a
            where a.id <> :appointmentId
              and a.doctor.id = :doctorId
              and a.status in :statuses
              and a.scheduledStart < :end
              and a.scheduledEnd > :start
            """)
    boolean existsActiveOverlapExcludingAppointment(
            @Param("appointmentId") Long appointmentId,
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );

    @Query("""
            select a from Appointment a
            where a.status = com.smartclinic.appointment.entity.AppointmentStatus.BOOKED
              and (a.reminderSent = false or a.reminderSent is null)
              and a.scheduledStart >= :start
              and a.scheduledStart <= :end
            """)
    List<Appointment> findAppointmentsForReminder(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
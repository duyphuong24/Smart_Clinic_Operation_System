package com.smartclinic.queue.repository;

import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.entity.QueueStatus;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QueueItemRepository extends JpaRepository<QueueItem, Long> {

    boolean existsByAppointmentId(Long appointmentId);

    boolean existsByQueueDateAndQueueNumber(LocalDate queueDate, Integer queueNumber);

    Optional<QueueItem> findTopByQueueDateOrderByQueueNumberDesc(LocalDate queueDate);

    @EntityGraph(attributePaths = {"patient", "doctor", "doctor.staff", "doctor.staff.user", "room", "appointment"})
    List<QueueItem> findByQueueDateAndStatusNotInOrderByQueueNumberAsc(
            LocalDate queueDate,
            Collection<QueueStatus> excludedStatuses
    );

    @EntityGraph(attributePaths = {"patient", "doctor", "doctor.staff", "doctor.staff.user", "room", "appointment"})
    @Query("""
            select q from QueueItem q
            where q.queueDate = :queueDate
              and q.doctor.id = :doctorId
              and q.status not in :excludedStatuses
            order by q.queueNumber asc
            """)
    List<QueueItem> findActiveByDateAndDoctor(
            @Param("queueDate") LocalDate queueDate,
            @Param("doctorId") Long doctorId,
            @Param("excludedStatuses") Collection<QueueStatus> excludedStatuses
    );

    @EntityGraph(attributePaths = {"patient", "doctor", "doctor.staff", "doctor.staff.user", "room", "appointment"})
    List<QueueItem> findByQueueDateAndDoctorIdOrderByQueueNumberAsc(LocalDate queueDate, Long doctorId);

    @EntityGraph(attributePaths = {"patient", "doctor", "doctor.staff", "doctor.staff.user", "room", "appointment"})
    List<QueueItem> findByDoctorIdOrderByQueueNumberAsc(Long doctorId);
}
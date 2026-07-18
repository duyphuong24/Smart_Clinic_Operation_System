package com.smartclinic.visit.repository;

import com.smartclinic.visit.entity.Visit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findByQueueItemId(Long queueItemId);

    boolean existsByVisitCode(String visitCode);

    boolean existsByAppointmentId(Long appointmentId);

    boolean existsByQueueItemId(Long queueItemId);

    Optional<Visit> findTopByOrderByIdDesc();
}
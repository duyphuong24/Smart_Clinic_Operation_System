package com.smartclinic.visit.entity;

import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.common.entity.BaseEntity;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.patient.entity.Patient;
import com.smartclinic.queue.entity.QueueItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "visits",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_visits_code", columnNames = "visit_code"),
                @UniqueConstraint(name = "uq_visits_appointment_id", columnNames = "appointment_id"),
                @UniqueConstraint(name = "uq_visits_queue_item_id", columnNames = "queue_item_id")
        }
)
@Getter
@Setter
public class Visit extends BaseEntity {

    @Column(name = "visit_code", nullable = false, unique = true, length = 50)
    private String visitCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_item_id")
    private QueueItem queueItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private VisitStatus status = VisitStatus.WAITING;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}
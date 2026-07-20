package com.smartclinic.encounter.entity;

import com.smartclinic.common.entity.BaseEntity;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.visit.entity.Visit;
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
        name = "encounters",
        uniqueConstraints = @UniqueConstraint(name = "uq_encounters_visit_id", columnNames = "visit_id")
)
@Getter
@Setter
public class Encounter extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false, unique = true)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "chief_complaint", columnDefinition = "nvarchar(max)")
    private String chiefComplaint;

    @Column(name = "diagnosis", columnDefinition = "nvarchar(max)")
    private String diagnosis;

    @Column(name = "clinical_note", columnDefinition = "nvarchar(max)")
    private String clinicalNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private EncounterStatus status = EncounterStatus.OPEN;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
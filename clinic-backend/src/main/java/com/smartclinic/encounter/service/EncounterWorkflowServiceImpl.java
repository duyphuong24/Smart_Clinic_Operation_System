package com.smartclinic.encounter.service;

import com.smartclinic.appointment.entity.AppointmentStatus;
import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.encounter.dto.EncounterCreateRequest;
import com.smartclinic.encounter.dto.EncounterResponse;
import com.smartclinic.encounter.dto.EncounterUpdateRequest;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.entity.EncounterStatus;
import com.smartclinic.encounter.mapper.EncounterMapper;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.visit.entity.Visit;
import com.smartclinic.visit.entity.VisitStatus;
import com.smartclinic.visit.repository.VisitRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EncounterWorkflowServiceImpl implements EncounterWorkflowService {

    private final EncounterRepository encounterRepository;
    private final VisitRepository visitRepository;
    private final AppointmentRepository appointmentRepository;
    private final QueueItemRepository queueItemRepository;

    @Override
    @Transactional(readOnly = true)
    public EncounterResponse getById(Long id) {
        return EncounterMapper.toResponse(findEncounter(id));
    }

    @Override
    public EncounterResponse create(EncounterCreateRequest request) {
        Visit visit = visitRepository.findById(request.getVisitId())
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        if (visit.getStatus() != VisitStatus.IN_CONSULTATION) {
            throw new BadRequestException("Only IN_CONSULTATION visit can open encounter");
        }
        if (encounterRepository.existsByVisitId(visit.getId())) {
            throw new DuplicateResourceException("Visit already has an encounter");
        }
        Encounter encounter = new Encounter();
        encounter.setVisit(visit);
        encounter.setDoctor(visit.getDoctor());
        encounter.setChiefComplaint(request.getChiefComplaint());
        encounter.setDiagnosis(request.getDiagnosis());
        encounter.setClinicalNote(request.getClinicalNote());
        encounter.setStatus(EncounterStatus.OPEN);
        encounter.setStartedAt(LocalDateTime.now());
        return EncounterMapper.toResponse(encounterRepository.save(encounter));
    }

    @Override
    public EncounterResponse update(Long id, EncounterUpdateRequest request) {
        Encounter encounter = findEncounter(id);
        assertOpen(encounter);
        encounter.setChiefComplaint(request.getChiefComplaint());
        encounter.setDiagnosis(request.getDiagnosis());
        encounter.setClinicalNote(request.getClinicalNote());
        return EncounterMapper.toResponse(encounterRepository.save(encounter));
    }

    @Override
    public EncounterResponse complete(Long id) {
        Encounter encounter = findEncounter(id);
        assertOpen(encounter);
        LocalDateTime now = LocalDateTime.now();
        encounter.setStatus(EncounterStatus.COMPLETED);
        encounter.setCompletedAt(now);
        Visit visit = encounter.getVisit();
        visit.setStatus(VisitStatus.COMPLETED);
        visit.setEndedAt(now);
        visitRepository.save(visit);
        if (visit.getAppointment() != null) {
            visit.getAppointment().setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.save(visit.getAppointment());
        }
        if (visit.getQueueItem() != null) {
            visit.getQueueItem().setStatus(QueueStatus.DONE);
            queueItemRepository.save(visit.getQueueItem());
        }
        return EncounterMapper.toResponse(encounterRepository.save(encounter));
    }

    private Encounter findEncounter(Long id) {
        return encounterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Encounter not found"));
    }

    private void assertOpen(Encounter encounter) {
        if (encounter.getStatus() != EncounterStatus.OPEN) {
            throw new BadRequestException("Completed encounter is read-only");
        }
    }
}
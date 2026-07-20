package com.smartclinic.visit.service;

import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.appointment.entity.AppointmentStatus;
import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.visit.dto.VisitCreateRequest;
import com.smartclinic.visit.dto.VisitResponse;
import com.smartclinic.visit.entity.Visit;
import com.smartclinic.visit.entity.VisitStatus;
import com.smartclinic.visit.mapper.VisitMapper;
import com.smartclinic.visit.repository.VisitRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final AppointmentRepository appointmentRepository;
    private final QueueItemRepository queueItemRepository;

    @Override
    @Transactional(readOnly = true)
    public VisitResponse getById(Long id) {
        return VisitMapper.toResponse(findVisit(id));
    }

    @Override
    public VisitResponse create(VisitCreateRequest request) {
        if (request.getQueueItemId() != null) {
            return VisitMapper.toResponse(createFromQueueItem(request.getQueueItemId()));
        }
        return VisitMapper.toResponse(createFromAppointment(request.getAppointmentId()));
    }

    private Visit createFromQueueItem(Long queueItemId) {
        QueueItem queueItem = queueItemRepository.findById(queueItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue item not found"));
        if (queueItem.getStatus() != QueueStatus.IN_SERVICE) {
            throw new BadRequestException("Only IN_SERVICE queue item can create visit");
        }
        if (visitRepository.existsByQueueItemId(queueItemId)) {
            throw new DuplicateResourceException("Queue item already has a visit");
        }
        Appointment appointment = queueItem.getAppointment();
        if (appointment != null && visitRepository.existsByAppointmentId(appointment.getId())) {
            throw new DuplicateResourceException("Appointment already has a visit");
        }
        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.IN_CONSULTATION);
            appointmentRepository.save(appointment);
        }
        Visit visit = new Visit();
        visit.setVisitCode(generateVisitCode());
        visit.setPatient(queueItem.getPatient());
        visit.setDoctor(queueItem.getDoctor());
        visit.setAppointment(appointment);
        visit.setQueueItem(queueItem);
        visit.setStatus(VisitStatus.IN_CONSULTATION);
        visit.setStartedAt(LocalDateTime.now());
        return visitRepository.save(visit);
    }

    private Visit createFromAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (appointment.getStatus() != AppointmentStatus.CHECKED_IN
                && appointment.getStatus() != AppointmentStatus.IN_CONSULTATION) {
            throw new BadRequestException("Only checked-in appointment can create visit");
        }
        if (visitRepository.existsByAppointmentId(appointmentId)) {
            throw new DuplicateResourceException("Appointment already has a visit");
        }
        appointment.setStatus(AppointmentStatus.IN_CONSULTATION);
        appointmentRepository.save(appointment);
        Visit visit = new Visit();
        visit.setVisitCode(generateVisitCode());
        visit.setPatient(appointment.getPatient());
        visit.setDoctor(appointment.getDoctor());
        visit.setAppointment(appointment);
        visit.setStatus(VisitStatus.IN_CONSULTATION);
        visit.setStartedAt(LocalDateTime.now());
        return visitRepository.save(visit);
    }

    private Visit findVisit(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
    }

    private String generateVisitCode() {
        long nextNumber = visitRepository.findTopByOrderByIdDesc()
                .map(existing -> existing.getId() + 1)
                .orElse(1L);
        String code;
        do {
            code = "VIS-%06d".formatted(nextNumber++);
        } while (visitRepository.existsByVisitCode(code));
        return code;
    }
}
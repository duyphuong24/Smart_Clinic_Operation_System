package com.smartclinic.queue.service;

import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.appointment.entity.AppointmentStatus;
import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.repository.RoomRepository;
import com.smartclinic.patient.entity.Patient;
import com.smartclinic.patient.entity.PatientStatus;
import com.smartclinic.patient.repository.PatientRepository;
import com.smartclinic.queue.dto.AppointmentCheckInRequest;
import com.smartclinic.queue.dto.QueueItemResponse;
import com.smartclinic.queue.dto.QueueTransferRequest;
import com.smartclinic.queue.dto.WalkInQueueRequest;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.entity.QueuePriority;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.mapper.QueueItemMapper;
import com.smartclinic.queue.repository.QueueItemRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QueueItemServiceImpl implements QueueItemService {

    private static final List<QueueStatus> CLOSED_STATUSES = List.of(QueueStatus.DONE, QueueStatus.SKIPPED);

    private final QueueItemRepository queueItemRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QueueItemResponse> findActive(LocalDate date, Long doctorId) {
        LocalDate queueDate = date == null ? LocalDate.now() : date;
        List<QueueItem> items = doctorId == null
                ? queueItemRepository.findByQueueDateAndStatusNotInOrderByQueueNumberAsc(queueDate, CLOSED_STATUSES)
                : queueItemRepository.findActiveByDateAndDoctor(queueDate, doctorId, CLOSED_STATUSES);
        return items.stream().map(QueueItemMapper::toResponse).toList();
    }

    @Override
    public QueueItemResponse checkIn(AppointmentCheckInRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new BadRequestException("Only BOOKED appointment can be checked in");
        }
        if (queueItemRepository.existsByAppointmentId(appointment.getId())) {
            throw new DuplicateResourceException("Appointment already has a queue item");
        }
        LocalDate queueDate = LocalDate.now();
        QueueItem item = QueueItem.builder()
                .queueDate(queueDate)
                .queueNumber(generateQueueNumber(queueDate))
                .patient(appointment.getPatient())
                .appointment(appointment)
                .doctor(appointment.getDoctor())
                .room(appointment.getRoom())
                .status(QueueStatus.WAITING)
                .priority(request.getPriority() == null ? QueuePriority.NORMAL : request.getPriority())
                .reason(appointment.getReason())
                .build();
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointmentRepository.save(appointment);
        return QueueItemMapper.toResponse(queueItemRepository.save(item));
    }

    @Override
    public QueueItemResponse createWalkIn(WalkInQueueRequest request) {
        Patient patient = findActivePatient(request.getPatientId());
        Doctor doctor = findActiveDoctor(request.getDoctorId());
        Room room = request.getRoomId() == null ? findDoctorDefaultRoom(doctor) : findActiveRoom(request.getRoomId());
        LocalDate queueDate = LocalDate.now();
        QueueItem item = QueueItem.builder()
                .queueDate(queueDate)
                .queueNumber(generateQueueNumber(queueDate))
                .patient(patient)
                .doctor(doctor)
                .room(room)
                .status(QueueStatus.WAITING)
                .priority(request.getPriority() == null ? QueuePriority.NORMAL : request.getPriority())
                .reason(request.getReason())
                .build();
        return QueueItemMapper.toResponse(queueItemRepository.save(item));
    }

    @Override
    public QueueItemResponse call(Long id) {
        QueueItem item = findQueueItem(id);
        if (item.getStatus() != QueueStatus.WAITING) {
            throw new BadRequestException("Only WAITING queue item can be called");
        }
        item.setStatus(QueueStatus.CALLED);
        return QueueItemMapper.toResponse(queueItemRepository.save(item));
    }

    @Override
    public QueueItemResponse startService(Long id) {
        QueueItem item = findQueueItem(id);
        if (item.getStatus() != QueueStatus.CALLED) {
            throw new BadRequestException("Only CALLED queue item can start service");
        }
        item.setStatus(QueueStatus.IN_SERVICE);
        if (item.getAppointment() != null) {
            item.getAppointment().setStatus(AppointmentStatus.IN_CONSULTATION);
            appointmentRepository.save(item.getAppointment());
        }
        return QueueItemMapper.toResponse(queueItemRepository.save(item));
    }

    @Override
    public QueueItemResponse done(Long id) {
        QueueItem item = findQueueItem(id);
        if (item.getStatus() != QueueStatus.IN_SERVICE) {
            throw new BadRequestException("Only IN_SERVICE queue item can be done");
        }
        item.setStatus(QueueStatus.DONE);
        if (item.getAppointment() != null) {
            item.getAppointment().setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.save(item.getAppointment());
        }
        return QueueItemMapper.toResponse(queueItemRepository.save(item));
    }

    @Override
    public QueueItemResponse skip(Long id) {
        return skip(id, null);
    }

    @Override
    public QueueItemResponse skip(Long id, String reason) {
        QueueItem item = findQueueItem(id);
        if (item.getStatus() != QueueStatus.WAITING && item.getStatus() != QueueStatus.CALLED) {
            throw new BadRequestException("Only WAITING or CALLED queue item can be skipped");
        }
        item.setStatus(QueueStatus.SKIPPED);
        if (reason != null && !reason.isBlank()) {
            item.setReason(reason);
        }
        if (item.getAppointment() != null) {
            item.getAppointment().setStatus(AppointmentStatus.CANCELLED);
            if (reason != null && !reason.isBlank()) {
                item.getAppointment().setCancelledReason(reason);
            }
            appointmentRepository.save(item.getAppointment());
        }
        return QueueItemMapper.toResponse(queueItemRepository.save(item));
    }

    @Override
    @Transactional
    public QueueItemResponse transferQueueItem(Long id, QueueTransferRequest request) {
        QueueItem item = findQueueItem(id);
        if (item.getStatus() != QueueStatus.WAITING && item.getStatus() != QueueStatus.CALLED) {
            throw new BadRequestException("Only WAITING or CALLED queue item can be transferred");
        }
        Doctor targetDoctor = findActiveDoctor(request.getTargetDoctorId());
        Room targetRoom = request.getTargetRoomId() != null
                ? findActiveRoom(request.getTargetRoomId())
                : findDoctorDefaultRoom(targetDoctor);

        int nextNumber = generateQueueNumber(item.getQueueDate());

        item.setDoctor(targetDoctor);
        item.setRoom(targetRoom);
        item.setQueueNumber(nextNumber);
        item.setStatus(QueueStatus.WAITING);

        return QueueItemMapper.toResponse(queueItemRepository.save(item));
    }

    @Override
    @Transactional
    public QueueItemResponse reQueueItem(Long id) {
        QueueItem item = findQueueItem(id);
        if (item.getStatus() != QueueStatus.SKIPPED) {
            throw new BadRequestException("Only SKIPPED queue item can be re-queued");
        }
        item.setStatus(QueueStatus.WAITING);
        return QueueItemMapper.toResponse(queueItemRepository.save(item));
    }


    private QueueItem findQueueItem(Long id) {
        return queueItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue item not found"));
    }

    private Patient findActivePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        if (patient.getStatus() != PatientStatus.ACTIVE) {
            throw new BadRequestException("Inactive patient cannot enter queue");
        }
        return patient;
    }

    private Doctor findActiveDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        if (!doctor.isActive()) {
            throw new BadRequestException("Inactive doctor cannot be used for queue");
        }
        return doctor;
    }

    private Room findDoctorDefaultRoom(Doctor doctor) {
        Room room = doctor.getDefaultRoom();
        if (room == null) {
            throw new BadRequestException("Queue room is required when doctor has no default room");
        }
        if (!room.isActive()) {
            throw new BadRequestException("Inactive room cannot be used for queue");
        }
        return room;
    }

    private Room findActiveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.isActive()) {
            throw new BadRequestException("Inactive room cannot be used for queue");
        }
        return room;
    }

    private Integer generateQueueNumber(LocalDate queueDate) {
        int next = queueItemRepository.findTopByQueueDateOrderByQueueNumberDesc(queueDate)
                .map(item -> item.getQueueNumber() + 1)
                .orElse(1);
        while (queueItemRepository.existsByQueueDateAndQueueNumber(queueDate, next)) {
            next++;
        }
        return next;
    }
}
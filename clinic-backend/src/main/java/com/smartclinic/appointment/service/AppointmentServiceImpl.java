package com.smartclinic.appointment.service;

import com.smartclinic.appointment.dto.AppointmentRequest;
import com.smartclinic.appointment.dto.AppointmentRescheduleRequest;
import com.smartclinic.appointment.dto.AppointmentResponse;
import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.appointment.entity.AppointmentStatus;
import com.smartclinic.appointment.mapper.AppointmentMapper;
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
import com.smartclinic.schedule.repository.DoctorAvailabilityRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private static final List<AppointmentStatus> ACTIVE_SLOT_STATUSES = List.of(
            AppointmentStatus.BOOKED,
            AppointmentStatus.CHECKED_IN,
            AppointmentStatus.IN_CONSULTATION
    );

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final RoomRepository roomRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final com.smartclinic.notification.service.NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll(LocalDate date) {
        List<Appointment> appointments;
        if (date == null) {
            appointments = appointmentRepository.findAll();
        } else {
            appointments = appointmentRepository.findByScheduledStartBetweenOrderByScheduledStartAsc(
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay()
            );
        }
        return appointments.stream().map(AppointmentMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getById(Long id) {
        return AppointmentMapper.toResponse(findAppointment(id));
    }

    @Override
    public AppointmentResponse create(AppointmentRequest request) {
        validateTimeRange(request.getScheduledStart(), request.getScheduledEnd());
        Patient patient = findActivePatient(request.getPatientId());
        Doctor doctor = findActiveDoctor(request.getDoctorId());
        Room room = findRoomForAppointment(request.getRoomId(), doctor);
        validateAvailability(doctor.getId(), request.getScheduledStart(), request.getScheduledEnd());
        if (appointmentRepository.existsActiveOverlap(
                doctor.getId(),
                request.getScheduledStart(),
                request.getScheduledEnd(),
                ACTIVE_SLOT_STATUSES
        )) {
            throw new DuplicateResourceException("Doctor already has an active appointment in this slot");
        }
        Appointment appointment = Appointment.builder()
                .appointmentCode(generateAppointmentCode())
                .patient(patient)
                .doctor(doctor)
                .room(room)
                .scheduledStart(request.getScheduledStart())
                .scheduledEnd(request.getScheduledEnd())
                .reason(request.getReason())
                .source(request.getSource())
                .status(AppointmentStatus.BOOKED)
                .build();
        Appointment saved = appointmentRepository.save(appointment);
        if (notificationService != null) {
            notificationService.sendAppointmentConfirmation(saved);
        }
        return AppointmentMapper.toResponse(saved);
    }

    @Override
    public AppointmentResponse reschedule(Long id, AppointmentRescheduleRequest request) {
        Appointment appointment = findAppointment(id);
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new BadRequestException("Only BOOKED appointment can be rescheduled");
        }
        validateTimeRange(request.getScheduledStart(), request.getScheduledEnd());
        Doctor doctor = request.getDoctorId() == null ? appointment.getDoctor() : findActiveDoctor(request.getDoctorId());
        Room room = request.getRoomId() == null ? findRoomForAppointment(null, doctor) : findActiveRoom(request.getRoomId());
        validateAvailability(doctor.getId(), request.getScheduledStart(), request.getScheduledEnd());
        if (appointmentRepository.existsActiveOverlapExcludingAppointment(
                appointment.getId(),
                doctor.getId(),
                request.getScheduledStart(),
                request.getScheduledEnd(),
                ACTIVE_SLOT_STATUSES
        )) {
            throw new DuplicateResourceException("Doctor already has an active appointment in this slot");
        }
        appointment.setDoctor(doctor);
        appointment.setRoom(room);
        appointment.setScheduledStart(request.getScheduledStart());
        appointment.setScheduledEnd(request.getScheduledEnd());
        return AppointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse cancel(Long id, String reason) {
        Appointment appointment = findAppointment(id);
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new BadRequestException("Only BOOKED appointment can be cancelled");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledReason(reason);
        return AppointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse markNoShow(Long id) {
        Appointment appointment = findAppointment(id);
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new BadRequestException("Only BOOKED appointment can be marked as NO_SHOW");
        }
        appointment.setStatus(AppointmentStatus.NO_SHOW);
        return AppointmentMapper.toResponse(appointmentRepository.save(appointment));
    }


    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    private Patient findActivePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        if (patient.getStatus() != PatientStatus.ACTIVE) {
            throw new BadRequestException("Inactive patient cannot book appointment");
        }
        return patient;
    }

    private Doctor findActiveDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        if (!doctor.isActive()) {
            throw new BadRequestException("Inactive doctor cannot be used for appointment");
        }
        return doctor;
    }

    private Room findRoomForAppointment(Long roomId, Doctor doctor) {
        if (roomId != null) {
            return findActiveRoom(roomId);
        }
        Room defaultRoom = doctor.getDefaultRoom();
        if (defaultRoom == null) {
            throw new BadRequestException("Appointment room is required when doctor has no default room");
        }
        if (!defaultRoom.isActive()) {
            throw new BadRequestException("Inactive room cannot be used for appointment");
        }
        return defaultRoom;
    }

    private Room findActiveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.isActive()) {
            throw new BadRequestException("Inactive room cannot be used for appointment");
        }
        return room;
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new BadRequestException("Appointment start time must be before end time");
        }
    }

    private void validateAvailability(Long doctorId, LocalDateTime start, LocalDateTime end) {
        DayOfWeek dayOfWeek = start.getDayOfWeek();
        boolean covered = availabilityRepository.existsActiveAvailabilityCovering(
                doctorId,
                dayOfWeek.getValue(),
                start.toLocalTime(),
                end.toLocalTime()
        );
        if (!covered) {
            throw new BadRequestException("Appointment must be inside active doctor availability");
        }
    }

    private String generateAppointmentCode() {
        long nextNumber = appointmentRepository.findTopByOrderByIdDesc()
                .map(existing -> existing.getId() + 1)
                .orElse(1L);
        String code;
        do {
            code = "APT-%06d".formatted(nextNumber++);
        } while (appointmentRepository.existsByAppointmentCode(code));
        return code;
    }
}
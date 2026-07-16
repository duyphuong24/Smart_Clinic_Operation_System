package com.smartclinic.schedule.service;

import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.repository.RoomRepository;
import com.smartclinic.schedule.dto.DoctorAvailabilityRequest;
import com.smartclinic.schedule.dto.DoctorAvailabilityResponse;
import com.smartclinic.schedule.entity.DoctorAvailability;
import com.smartclinic.schedule.mapper.DoctorAvailabilityMapper;
import com.smartclinic.schedule.repository.DoctorAvailabilityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DoctorAvailabilityResponse> findAll(Long doctorId) {
        if (doctorId != null) {
            return availabilityRepository.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(doctorId).stream()
                    .map(DoctorAvailabilityMapper::toResponse)
                    .toList();
        }
        return availabilityRepository.findAll().stream()
                .map(DoctorAvailabilityMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorAvailabilityResponse getById(Long id) {
        return DoctorAvailabilityMapper.toResponse(findAvailability(id));
    }

    @Override
    public DoctorAvailabilityResponse create(DoctorAvailabilityRequest request) {
        validateTimeRange(request);
        DoctorAvailability availability = DoctorAvailability.builder()
                .doctor(findActiveDoctor(request.getDoctorId()))
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .slotMinutes(request.getSlotMinutes())
                .room(findActiveRoom(request.getRoomId()))
                .active(request.isActive())
                .build();
        return DoctorAvailabilityMapper.toResponse(availabilityRepository.save(availability));
    }

    @Override
    public DoctorAvailabilityResponse update(Long id, DoctorAvailabilityRequest request) {
        validateTimeRange(request);
        DoctorAvailability availability = findAvailability(id);
        availability.setDoctor(findActiveDoctor(request.getDoctorId()));
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setSlotMinutes(request.getSlotMinutes());
        availability.setRoom(findActiveRoom(request.getRoomId()));
        availability.setActive(request.isActive());
        return DoctorAvailabilityMapper.toResponse(availabilityRepository.save(availability));
    }

    @Override
    public void deactivate(Long id) {
        DoctorAvailability availability = findAvailability(id);
        availability.setActive(false);
        availabilityRepository.save(availability);
    }

    private DoctorAvailability findAvailability(Long id) {
        return availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor availability not found"));
    }

    private void validateTimeRange(DoctorAvailabilityRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BadRequestException("Availability start time must be before end time");
        }
    }

    private Doctor findActiveDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        if (!doctor.isActive()) {
            throw new BadRequestException("Inactive doctor cannot be used for availability");
        }
        return doctor;
    }

    private Room findActiveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.isActive()) {
            throw new BadRequestException("Inactive room cannot be used for availability");
        }
        return room;
    }
}
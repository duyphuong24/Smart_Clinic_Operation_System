package com.smartclinic.doctor.service;

import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.doctor.dto.DoctorRequest;
import com.smartclinic.doctor.dto.DoctorResponse;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.mapper.DoctorMapper;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.entity.Specialty;
import com.smartclinic.masterdata.repository.RoomRepository;
import com.smartclinic.masterdata.repository.SpecialtyRepository;
import com.smartclinic.staff.entity.Staff;
import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.entity.StaffType;
import com.smartclinic.staff.repository.StaffRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final StaffRepository staffRepository;
    private final SpecialtyRepository specialtyRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> findAll() {
        return doctorRepository.findAll().stream()
                .map(DoctorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getById(Long id) {
        return DoctorMapper.toResponse(findDoctor(id));
    }

    @Override
    public DoctorResponse create(DoctorRequest request) {
        if (doctorRepository.existsByStaffId(request.getStaffId())) {
            throw new DuplicateResourceException("Staff already has a doctor profile");
        }
        if (doctorRepository.existsByLicenseNoIgnoreCase(request.getLicenseNo())) {
            throw new DuplicateResourceException("Doctor license number already exists");
        }
        Doctor doctor = Doctor.builder()
                .staff(findActiveDoctorStaff(request.getStaffId()))
                .specialty(findActiveSpecialty(request.getSpecialtyId()))
                .defaultRoom(findActiveRoomOrNull(request.getDefaultRoomId()))
                .licenseNo(request.getLicenseNo().trim())
                .consultationFee(request.getConsultationFee())
                .bio(request.getBio())
                .active(request.isActive())
                .build();
        return DoctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    public DoctorResponse update(Long id, DoctorRequest request) {
        Doctor doctor = findDoctor(id);
        if (!doctor.getStaff().getId().equals(request.getStaffId())
                && doctorRepository.existsByStaffId(request.getStaffId())) {
            throw new DuplicateResourceException("Staff already has a doctor profile");
        }
        if (!doctor.getLicenseNo().equalsIgnoreCase(request.getLicenseNo())
                && doctorRepository.existsByLicenseNoIgnoreCase(request.getLicenseNo())) {
            throw new DuplicateResourceException("Doctor license number already exists");
        }
        doctor.setStaff(findActiveDoctorStaff(request.getStaffId()));
        doctor.setSpecialty(findActiveSpecialty(request.getSpecialtyId()));
        doctor.setDefaultRoom(findActiveRoomOrNull(request.getDefaultRoomId()));
        doctor.setLicenseNo(request.getLicenseNo().trim());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setBio(request.getBio());
        doctor.setActive(request.isActive());
        return DoctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    public void deactivate(Long id) {
        Doctor doctor = findDoctor(id);
        doctor.setActive(false);
        doctorRepository.save(doctor);
    }

    private Doctor findDoctor(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    private Staff findActiveDoctorStaff(Long staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        if (staff.getStaffType() != StaffType.DOCTOR || staff.getStatus() != StaffStatus.ACTIVE) {
            throw new BadRequestException("Doctor profile requires an active DOCTOR staff profile");
        }
        return staff;
    }

    private Specialty findActiveSpecialty(Long specialtyId) {
        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found"));
        if (!specialty.isActive()) {
            throw new BadRequestException("Inactive specialty cannot be assigned to doctor");
        }
        return specialty;
    }

    private Room findActiveRoomOrNull(Long roomId) {
        if (roomId == null) {
            return null;
        }
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.isActive()) {
            throw new BadRequestException("Inactive room cannot be assigned to doctor");
        }
        return room;
    }
}
package com.smartclinic.staff.service;

import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.staff.dto.StaffRequest;
import com.smartclinic.staff.dto.StaffResponse;
import com.smartclinic.staff.entity.Staff;
import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.mapper.StaffMapper;
import com.smartclinic.staff.repository.StaffRepository;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> findAll() {
        return staffRepository.findAll().stream()
                .map(StaffMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getById(Long id) {
        return StaffMapper.toResponse(findStaff(id));
    }

    @Override
    public StaffResponse create(StaffRequest request) {
        if (staffRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new DuplicateResourceException("Employee code already exists");
        }
        if (staffRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateResourceException("User already has a staff profile");
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Staff staff = Staff.builder()
                .user(user)
                .employeeCode(request.getEmployeeCode().trim())
                .staffType(request.getStaffType())
                .hiredDate(request.getHiredDate())
                .status(request.getStatus() == null ? StaffStatus.ACTIVE : request.getStatus())
                .build();
        return StaffMapper.toResponse(staffRepository.save(staff));
    }

    @Override
    public StaffResponse update(Long id, StaffRequest request) {
        Staff staff = findStaff(id);
        if (!staff.getEmployeeCode().equalsIgnoreCase(request.getEmployeeCode())
                && staffRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new DuplicateResourceException("Employee code already exists");
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        staffRepository.findByUserId(request.getUserId())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("User already has a staff profile");
                });
        staff.setUser(user);
        staff.setEmployeeCode(request.getEmployeeCode().trim());
        staff.setStaffType(request.getStaffType());
        staff.setHiredDate(request.getHiredDate());
        staff.setStatus(request.getStatus() == null ? StaffStatus.ACTIVE : request.getStatus());
        return StaffMapper.toResponse(staffRepository.save(staff));
    }

    @Override
    public void deactivate(Long id) {
        Staff staff = findStaff(id);
        staff.setStatus(StaffStatus.INACTIVE);
        staffRepository.save(staff);
    }

    private Staff findStaff(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
    }
}
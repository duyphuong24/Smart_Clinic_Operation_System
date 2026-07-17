package com.smartclinic.masterdata.service;

import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.masterdata.dto.SpecialtyRequest;
import com.smartclinic.masterdata.dto.SpecialtyResponse;
import com.smartclinic.masterdata.entity.Specialty;
import com.smartclinic.masterdata.mapper.MasterDataMapper;
import com.smartclinic.masterdata.repository.SpecialtyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponse> findAll() {
        return specialtyRepository.findAll().stream()
                .map(MasterDataMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponse getById(Long id) {
        return MasterDataMapper.toResponse(findSpecialty(id));
    }

    @Override
    public SpecialtyResponse create(SpecialtyRequest request) {
        if (specialtyRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Specialty name already exists");
        }
        Specialty specialty = Specialty.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .active(request.isActive())
                .build();
        return MasterDataMapper.toResponse(specialtyRepository.save(specialty));
    }

    @Override
    public SpecialtyResponse update(Long id, SpecialtyRequest request) {
        Specialty specialty = findSpecialty(id);
        if (!specialty.getName().equalsIgnoreCase(request.getName())
                && specialtyRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Specialty name already exists");
        }
        specialty.setName(request.getName().trim());
        specialty.setDescription(request.getDescription());
        specialty.setActive(request.isActive());
        return MasterDataMapper.toResponse(specialtyRepository.save(specialty));
    }

    @Override
    public void deactivate(Long id) {
        Specialty specialty = findSpecialty(id);
        specialty.setActive(false);
        specialtyRepository.save(specialty);
    }

    private Specialty findSpecialty(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found"));
    }
}
package com.smartclinic.patient.service;

import com.smartclinic.common.api.PageResponse;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.patient.dto.PatientCreateRequest;
import com.smartclinic.patient.dto.PatientMedicalHistoryResponse;
import com.smartclinic.patient.dto.PatientResponse;
import com.smartclinic.patient.dto.PatientUpdateRequest;
import com.smartclinic.patient.entity.Patient;
import com.smartclinic.patient.entity.PatientStatus;
import com.smartclinic.patient.mapper.PatientMapper;
import com.smartclinic.patient.repository.PatientRepository;
import com.smartclinic.visit.dto.VisitResponse;
import com.smartclinic.visit.mapper.VisitMapper;
import com.smartclinic.visit.repository.VisitRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private static final String PATIENT_CODE_PREFIX = "PAT-";

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    @Override
    @Transactional
    public PatientResponse create(PatientCreateRequest request) {
        Patient patient = PatientMapper.toEntity(request);
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setPatientCode(generatePatientCode());
        return PatientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public PatientResponse update(Long id, PatientUpdateRequest request) {
        Patient patient = findPatient(id);
        PatientMapper.apply(patient, request);
        return PatientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getById(Long id) {
        return PatientMapper.toResponse(findPatient(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PatientResponse> search(String keyword, Pageable pageable) {
        Page<PatientResponse> page = patientRepository.search(normalizeKeyword(keyword), pageable)
                .map(PatientMapper::toResponse);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Patient patient = findPatient(id);
        patient.setStatus(PatientStatus.ARCHIVED);
        patientRepository.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientMedicalHistoryResponse getMedicalHistory(Long id) {
        Patient patient = findPatient(id);
        List<VisitResponse> visits = visitRepository.findByPatientIdOrderByIdDesc(id).stream()
                .map(VisitMapper::toResponse)
                .toList();

        return PatientMedicalHistoryResponse.builder()
                .patient(PatientMapper.toResponse(patient))
                .totalVisits(visits.size())
                .visits(visits)
                .build();
    }

    private Patient findPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
    }

    private String generatePatientCode() {
        long nextNumber = patientRepository.findTopByOrderByIdDesc()
                .map(patient -> patient.getId() == null ? 1L : patient.getId() + 1L)
                .orElse(1L);
        String candidate = PATIENT_CODE_PREFIX + String.format("%06d", nextNumber);

        while (patientRepository.existsByPatientCode(candidate)) {
            nextNumber++;
            candidate = PATIENT_CODE_PREFIX + String.format("%06d", nextNumber);
        }

        return candidate;
    }

    private String normalizeKeyword(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }
}
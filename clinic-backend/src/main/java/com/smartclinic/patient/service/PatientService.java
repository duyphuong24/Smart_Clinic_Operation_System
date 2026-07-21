package com.smartclinic.patient.service;

import com.smartclinic.common.api.PageResponse;
import com.smartclinic.patient.dto.PatientCreateRequest;
import com.smartclinic.patient.dto.PatientResponse;
import com.smartclinic.patient.dto.PatientUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface PatientService {

    PatientResponse create(PatientCreateRequest request);

    PatientResponse update(Long id, PatientUpdateRequest request);

    PatientResponse getById(Long id);

    PageResponse<PatientResponse> search(String keyword, Pageable pageable);
}
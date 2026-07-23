package com.smartclinic.doctor.service;

import com.smartclinic.doctor.dto.DoctorRequest;
import com.smartclinic.doctor.dto.DoctorResponse;
import java.util.List;

public interface DoctorService {

    List<DoctorResponse> findAll();

    DoctorResponse getById(Long id);

    DoctorResponse create(DoctorRequest request);

    DoctorResponse update(Long id, DoctorRequest request);

    void deactivate(Long id);
}
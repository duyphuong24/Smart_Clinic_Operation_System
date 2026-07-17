package com.smartclinic.schedule.service;

import com.smartclinic.schedule.dto.DoctorAvailabilityRequest;
import com.smartclinic.schedule.dto.DoctorAvailabilityResponse;
import java.util.List;

public interface DoctorAvailabilityService {

    List<DoctorAvailabilityResponse> findAll(Long doctorId);

    DoctorAvailabilityResponse getById(Long id);

    DoctorAvailabilityResponse create(DoctorAvailabilityRequest request);

    DoctorAvailabilityResponse update(Long id, DoctorAvailabilityRequest request);

    void deactivate(Long id);
}
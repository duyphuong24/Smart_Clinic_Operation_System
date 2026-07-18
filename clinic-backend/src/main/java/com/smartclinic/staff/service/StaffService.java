package com.smartclinic.staff.service;

import com.smartclinic.staff.dto.StaffRequest;
import com.smartclinic.staff.dto.StaffResponse;
import java.util.List;

public interface StaffService {

    List<StaffResponse> findAll();

    StaffResponse getById(Long id);

    StaffResponse create(StaffRequest request);

    StaffResponse update(Long id, StaffRequest request);

    void deactivate(Long id);
}
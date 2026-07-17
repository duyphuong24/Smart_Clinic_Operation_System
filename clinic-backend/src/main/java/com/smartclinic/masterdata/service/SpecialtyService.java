package com.smartclinic.masterdata.service;

import com.smartclinic.masterdata.dto.SpecialtyRequest;
import com.smartclinic.masterdata.dto.SpecialtyResponse;
import java.util.List;

public interface SpecialtyService {

    List<SpecialtyResponse> findAll();

    SpecialtyResponse getById(Long id);

    SpecialtyResponse create(SpecialtyRequest request);

    SpecialtyResponse update(Long id, SpecialtyRequest request);

    void deactivate(Long id);
}
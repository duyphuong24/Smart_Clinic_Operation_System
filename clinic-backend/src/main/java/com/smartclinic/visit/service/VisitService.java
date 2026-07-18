package com.smartclinic.visit.service;

import com.smartclinic.visit.dto.VisitCreateRequest;
import com.smartclinic.visit.dto.VisitResponse;

public interface VisitService {

    VisitResponse getById(Long id);

    VisitResponse create(VisitCreateRequest request);
}
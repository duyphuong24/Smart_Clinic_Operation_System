package com.smartclinic.masterdata.service;

import com.smartclinic.masterdata.dto.RoomRequest;
import com.smartclinic.masterdata.dto.RoomResponse;
import java.util.List;

public interface RoomService {

    List<RoomResponse> findAll();

    RoomResponse getById(Long id);

    RoomResponse create(RoomRequest request);

    RoomResponse update(Long id, RoomRequest request);

    void deactivate(Long id);
}
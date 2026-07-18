package com.smartclinic.queue.service;

import com.smartclinic.queue.dto.AppointmentCheckInRequest;
import com.smartclinic.queue.dto.QueueItemResponse;
import com.smartclinic.queue.dto.WalkInQueueRequest;
import java.time.LocalDate;
import java.util.List;

public interface QueueItemService {

    List<QueueItemResponse> findActive(LocalDate date, Long doctorId);

    QueueItemResponse checkIn(AppointmentCheckInRequest request);

    QueueItemResponse createWalkIn(WalkInQueueRequest request);

    QueueItemResponse call(Long id);

    QueueItemResponse startService(Long id);

    QueueItemResponse done(Long id);

    QueueItemResponse skip(Long id);
}
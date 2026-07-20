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

    @org.springframework.security.access.prepost.PreAuthorize("@securityHelper.canManageQueueItem(#id)")
    QueueItemResponse call(Long id);

    @org.springframework.security.access.prepost.PreAuthorize("@securityHelper.isQueueItemOwner(#id)")
    QueueItemResponse startService(Long id);

    @org.springframework.security.access.prepost.PreAuthorize("@securityHelper.isQueueItemOwner(#id)")
    QueueItemResponse done(Long id);

    @org.springframework.security.access.prepost.PreAuthorize("@securityHelper.canManageQueueItem(#id)")
    QueueItemResponse skip(Long id);
}
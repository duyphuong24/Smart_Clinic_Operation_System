package com.smartclinic.queue.mapper;

import com.smartclinic.queue.dto.QueueItemResponse;
import com.smartclinic.queue.entity.QueueItem;

public final class QueueItemMapper {

    private QueueItemMapper() {
    }

    public static QueueItemResponse toResponse(QueueItem item) {
        return QueueItemResponse.builder()
                .id(item.getId())
                .queueDate(item.getQueueDate())
                .queueNumber(item.getQueueNumber())
                .patientId(item.getPatient().getId())
                .patientCode(item.getPatient().getPatientCode())
                .patientName(item.getPatient().getFullName())
                .appointmentId(item.getAppointment() == null ? null : item.getAppointment().getId())
                .doctorId(item.getDoctor().getId())
                .doctorName(item.getDoctor().getStaff().getUser().getFullName())
                .roomId(item.getRoom().getId())
                .roomCode(item.getRoom().getRoomCode())
                .status(item.getStatus())
                .priority(item.getPriority())
                .reason(item.getReason())
                .build();
    }
}
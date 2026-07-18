package com.smartclinic.queue.dto;

import com.smartclinic.queue.entity.QueuePriority;
import com.smartclinic.queue.entity.QueueStatus;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueItemResponse {

    private final Long id;
    private final LocalDate queueDate;
    private final Integer queueNumber;
    private final Long patientId;
    private final String patientCode;
    private final String patientName;
    private final Long appointmentId;
    private final Long doctorId;
    private final String doctorName;
    private final Long roomId;
    private final String roomCode;
    private final QueueStatus status;
    private final QueuePriority priority;
    private final String reason;
}
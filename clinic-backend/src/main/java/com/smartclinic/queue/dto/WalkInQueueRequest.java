package com.smartclinic.queue.dto;

import com.smartclinic.queue.entity.QueuePriority;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalkInQueueRequest {

    @NotNull
    private Long patientId;

    @NotNull
    private Long doctorId;

    private Long roomId;

    private QueuePriority priority = QueuePriority.NORMAL;

    private String reason;
}
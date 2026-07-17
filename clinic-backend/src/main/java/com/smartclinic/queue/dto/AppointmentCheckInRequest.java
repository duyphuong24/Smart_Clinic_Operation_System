package com.smartclinic.queue.dto;

import com.smartclinic.queue.entity.QueuePriority;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentCheckInRequest {

    @NotNull
    private Long appointmentId;

    private QueuePriority priority = QueuePriority.NORMAL;
}
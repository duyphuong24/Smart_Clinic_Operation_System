package com.smartclinic.queue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueueTransferRequest {

    @NotNull(message = "Target doctor ID is required")
    private Long targetDoctorId;

    private Long targetRoomId;
}

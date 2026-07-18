package com.smartclinic.visit.dto;

import com.smartclinic.visit.entity.VisitStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VisitResponse {

    private final Long id;
    private final String visitCode;
    private final Long patientId;
    private final String patientCode;
    private final String patientName;
    private final Long doctorId;
    private final String doctorName;
    private final Long appointmentId;
    private final Long queueItemId;
    private final VisitStatus status;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
}
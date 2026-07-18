package com.smartclinic.visit.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitCreateRequest {

    private Long appointmentId;

    private Long queueItemId;

    @AssertTrue(message = "Either appointmentId or queueItemId is required")
    public boolean hasSource() {
        return appointmentId != null || queueItemId != null;
    }
}
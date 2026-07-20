package com.smartclinic.visit.mapper;

import com.smartclinic.visit.dto.VisitResponse;
import com.smartclinic.visit.entity.Visit;

public final class VisitMapper {

    private VisitMapper() {
    }

    public static VisitResponse toResponse(Visit visit) {
        return VisitResponse.builder()
                .id(visit.getId())
                .visitCode(visit.getVisitCode())
                .patientId(visit.getPatient().getId())
                .patientCode(visit.getPatient().getPatientCode())
                .patientName(visit.getPatient().getFullName())
                .doctorId(visit.getDoctor().getId())
                .doctorName(visit.getDoctor().getStaff().getUser().getFullName())
                .appointmentId(visit.getAppointment() == null ? null : visit.getAppointment().getId())
                .queueItemId(visit.getQueueItem() == null ? null : visit.getQueueItem().getId())
                .status(visit.getStatus())
                .startedAt(visit.getStartedAt())
                .endedAt(visit.getEndedAt())
                .build();
    }
}
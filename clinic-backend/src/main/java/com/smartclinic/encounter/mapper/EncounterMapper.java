package com.smartclinic.encounter.mapper;

import com.smartclinic.encounter.dto.EncounterResponse;
import com.smartclinic.encounter.entity.Encounter;

public final class EncounterMapper {

    private EncounterMapper() {
    }

    public static EncounterResponse toResponse(Encounter encounter) {
        return EncounterResponse.builder()
                .id(encounter.getId())
                .visitId(encounter.getVisit().getId())
                .visitCode(encounter.getVisit().getVisitCode())
                .patientId(encounter.getVisit().getPatient().getId())
                .patientName(encounter.getVisit().getPatient().getFullName())
                .doctorId(encounter.getDoctor().getId())
                .doctorName(encounter.getDoctor().getStaff().getUser().getFullName())
                .chiefComplaint(encounter.getChiefComplaint())
                .diagnosis(encounter.getDiagnosis())
                .clinicalNote(encounter.getClinicalNote())
                .status(encounter.getStatus())
                .startedAt(encounter.getStartedAt())
                .completedAt(encounter.getCompletedAt())
                .build();
    }
}
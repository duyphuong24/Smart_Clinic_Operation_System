package com.smartclinic.encounter.dto;

import com.smartclinic.encounter.entity.EncounterStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EncounterResponse {

    private final Long id;
    private final Long visitId;
    private final String visitCode;
    private final Long patientId;
    private final String patientName;
    private final Long doctorId;
    private final String doctorName;
    private final String chiefComplaint;
    private final String diagnosis;
    private final String clinicalNote;
    private final EncounterStatus status;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;
}
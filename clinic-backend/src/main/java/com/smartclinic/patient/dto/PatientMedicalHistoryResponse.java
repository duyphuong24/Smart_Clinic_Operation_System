package com.smartclinic.patient.dto;

import com.smartclinic.visit.dto.VisitResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PatientMedicalHistoryResponse {

    private PatientResponse patient;
    private int totalVisits;
    private List<VisitResponse> visits;
}

package com.smartclinic.patient.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.patient.dto.PatientCreateRequest;
import com.smartclinic.patient.dto.PatientResponse;
import com.smartclinic.patient.dto.PatientUpdateRequest;
import com.smartclinic.patient.entity.Patient;
import com.smartclinic.patient.repository.PatientRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private com.smartclinic.visit.repository.VisitRepository visitRepository;

    private PatientServiceImpl patientService;

    @BeforeEach
    void setUp() {
        patientService = new PatientServiceImpl(patientRepository, visitRepository);
    }

    @Test
    void createShouldGeneratePatientCodeAndSavePatient() {
        PatientCreateRequest request = new PatientCreateRequest();
        request.setFullName("Nguyen Van A");
        request.setPhone("0900000000");

        Patient saved = new Patient();
        saved.setId(1L);
        saved.setPatientCode("PAT-000001");
        saved.setFullName("Nguyen Van A");
        saved.setPhone("0900000000");

        when(patientRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(patientRepository.existsByPatientCode("PAT-000001")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        PatientResponse response = patientService.create(request);

        assertEquals("PAT-000001", response.getPatientCode());
        assertEquals("Nguyen Van A", response.getFullName());

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        assertEquals("PAT-000001", captor.getValue().getPatientCode());
    }

    @Test
    void updateShouldModifyExistingPatient() {
        Patient patient = new Patient();
        patient.setId(10L);
        patient.setPatientCode("PAT-000010");
        patient.setFullName("Old Name");

        PatientUpdateRequest request = new PatientUpdateRequest();
        request.setFullName("New Name");
        request.setPhone("0911111111");

        when(patientRepository.findById(10L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);

        PatientResponse response = patientService.update(10L, request);

        assertEquals("New Name", response.getFullName());
        assertEquals("0911111111", response.getPhone());
    }

    @Test
    void getByIdShouldThrowWhenPatientMissing() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.getById(99L));
    }

    @Test
    void searchShouldReturnPageResponse() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setPatientCode("PAT-000001");
        patient.setFullName("Nguyen Van A");

        PageRequest pageable = PageRequest.of(0, 10);
        when(patientRepository.search("Nguyen", pageable)).thenReturn(new PageImpl<>(List.of(patient), pageable, 1));

        var response = patientService.search(" Nguyen ", pageable);

        assertEquals(1, response.getItems().size());
        assertEquals(1, response.getTotalItems());
        assertEquals("PAT-000001", response.getItems().getFirst().getPatientCode());
    }
}
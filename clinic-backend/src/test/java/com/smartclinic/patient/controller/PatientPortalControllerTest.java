package com.smartclinic.patient.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.smartclinic.appointment.service.AppointmentService;
import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.masterdata.service.SpecialtyService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientPortalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private SpecialtyService specialtyService;

    @MockBean
    private AppointmentService appointmentService;

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void dashboard_ShouldReturnPatientPortalView() throws Exception {
        when(doctorService.findAll()).thenReturn(List.of());
        when(specialtyService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/my-portal"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/portal"))
                .andExpect(model().attributeExists("doctors", "specialties"));
    }

    @Test
    @WithMockUser(username = "patient1", roles = {"PATIENT"})
    void history_ShouldReturnMedicalHistoryView() throws Exception {
        when(appointmentService.findAll(any())).thenReturn(List.of());

        mockMvc.perform(get("/my-portal/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/history"))
                .andExpect(model().attributeExists("appointments"));
    }
}

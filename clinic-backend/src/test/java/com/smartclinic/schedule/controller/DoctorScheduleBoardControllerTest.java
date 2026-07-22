package com.smartclinic.schedule.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.masterdata.service.SpecialtyService;
import com.smartclinic.schedule.service.DoctorAvailabilityService;
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
class DoctorScheduleBoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorAvailabilityService doctorAvailabilityService;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private SpecialtyService specialtyService;

    @Test
    @WithMockUser(username = "receptionist", roles = {"RECEPTIONIST"})
    void board_ShouldReturnScheduleBoardView() throws Exception {
        when(doctorAvailabilityService.findAll(any())).thenReturn(List.of());
        when(doctorService.findAll()).thenReturn(List.of());
        when(specialtyService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/schedule/board"))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule/board"))
                .andExpect(model().attributeExists("availabilities", "doctors", "specialties"));
    }
}

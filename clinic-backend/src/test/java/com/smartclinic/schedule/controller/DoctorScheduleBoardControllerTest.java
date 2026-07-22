package com.smartclinic.schedule.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.masterdata.service.RoomService;
import com.smartclinic.masterdata.service.SpecialtyService;
import com.smartclinic.schedule.dto.DoctorAvailabilityRequest;
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

    @MockBean
    private RoomService roomService;

    @Test
    @WithMockUser(username = "receptionist", roles = {"RECEPTIONIST"})
    void board_ShouldReturnScheduleBoardView() throws Exception {
        when(doctorAvailabilityService.findAll(any())).thenReturn(List.of());
        when(doctorService.findAll()).thenReturn(List.of());
        when(specialtyService.findAll()).thenReturn(List.of());
        when(roomService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/schedule/board"))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule/board"))
                .andExpect(model().attributeExists("availabilities", "doctors", "specialties", "rooms", "newShift"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void saveShift_ShouldCreateAvailabilityAndRedirect() throws Exception {
        mockMvc.perform(post("/schedule/board/save")
                        .with(csrf())
                        .param("doctorId", "1")
                        .param("dayOfWeek", "1")
                        .param("roomId", "2")
                        .param("startTime", "08:00")
                        .param("endTime", "12:00")
                        .param("slotMinutes", "30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedule/board"));

        verify(doctorAvailabilityService).create(any(DoctorAvailabilityRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteShift_ShouldDeactivateAvailabilityAndRedirect() throws Exception {
        mockMvc.perform(post("/schedule/board/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedule/board"));

        verify(doctorAvailabilityService).deactivate(1L);
    }
}

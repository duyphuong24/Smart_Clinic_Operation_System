package com.smartclinic.doctor.controller;

import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DoctorPortalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorRepository doctorRepository;

    @MockBean
    private QueueItemRepository queueItemRepository;

    private Doctor sampleDoctor;

    @BeforeEach
    void setUp() {
        sampleDoctor = new Doctor();
        sampleDoctor.setId(1L);
        sampleDoctor.setActive(true);
    }

    @Test
    @WithMockUser(username = "doctor", roles = {"DOCTOR"})
    void myPatients_AsDoctor_ShouldReturnSuccess() throws Exception {
        when(doctorRepository.findByStaffUserUserName("doctor")).thenReturn(Optional.of(sampleDoctor));
        when(queueItemRepository.findByQueueDateAndDoctorIdOrderByQueueNumberAsc(any(LocalDate.class), eq(1L)))
                .thenReturn(List.of());

        mockMvc.perform(get("/doctor/my-patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/my-patients"))
                .andExpect(model().attributeExists("doctor"))
                .andExpect(model().attributeExists("queueItems"))
                .andExpect(model().attributeExists("waitingCount"))
                .andExpect(model().attributeExists("inServiceCount"))
                .andExpect(model().attributeExists("doneCount"))
                .andExpect(model().attributeExists("totalCount"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void myPatients_AsAdmin_ShouldReturnSuccessWithFallbackDoctor() throws Exception {
        when(doctorRepository.findByStaffUserUserName("admin")).thenReturn(Optional.empty());
        when(doctorRepository.findAll()).thenReturn(List.of(sampleDoctor));
        when(queueItemRepository.findByQueueDateAndDoctorIdOrderByQueueNumberAsc(any(LocalDate.class), eq(1L)))
                .thenReturn(List.of());

        mockMvc.perform(get("/doctor/my-patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/my-patients"))
                .andExpect(model().attributeExists("doctor"));
    }
}

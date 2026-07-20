package com.smartclinic.queue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.queue.dto.AppointmentCheckInRequest;
import com.smartclinic.queue.entity.QueuePriority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QueueAndAppointmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "receptionist", roles = {"RECEPTIONIST"})
    void testGetActiveQueueItemsSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/queue-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "receptionist", roles = {"RECEPTIONIST"})
    void testCheckInNonExistentAppointmentReturns404() throws Exception {
        AppointmentCheckInRequest checkInRequest = new AppointmentCheckInRequest();
        checkInRequest.setAppointmentId(999999L);
        checkInRequest.setPriority(QueuePriority.NORMAL);

        mockMvc.perform(post("/api/v1/queue-items/check-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testQueueItemTransitionsRestApi() throws Exception {
        // Test calling non-existent queue item returns 404
        mockMvc.perform(patch("/api/v1/queue-items/999999/call"))
                .andExpect(status().isNotFound());

        // Test start service on non-existent queue item returns 404
        mockMvc.perform(patch("/api/v1/queue-items/999999/start-service"))
                .andExpect(status().isNotFound());

        // Test done on non-existent queue item returns 404
        mockMvc.perform(patch("/api/v1/queue-items/999999/done"))
                .andExpect(status().isNotFound());
    }
}

package com.smartclinic.servicecatalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.smartclinic.servicecatalog.service.ServiceCatalogService;
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
class ServiceCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceCatalogService serviceCatalogService;

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void list_ShouldReturnServiceListView() throws Exception {
        when(serviceCatalogService.findAll(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/admin/services"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/service-list"))
                .andExpect(model().attributeExists("services", "serviceTypes"));
    }
}

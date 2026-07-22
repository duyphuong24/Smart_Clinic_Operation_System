package com.smartclinic.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.smartclinic.user.dto.ResetPasswordRequest;
import com.smartclinic.user.dto.UserCreateRequest;
import com.smartclinic.user.dto.UserStatusUpdateRequest;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.repository.RoleRepository;
import com.smartclinic.user.repository.UserRepository;
import com.smartclinic.user.service.UserService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void list_ShouldReturnUserListView() throws Exception {
        User user = new User();
        user.setUserName("admin");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(roleRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-list"))
                .andExpect(model().attributeExists("users", "roles"));
    }

    @Test
    void saveUser_ShouldCreateUserAndRedirect() throws Exception {
        mockMvc.perform(post("/admin/users/save")
                        .param("userName", "doctor10")
                        .param("fullName", "Dr. Ten")
                        .param("phone", "0900000000")
                        .param("password", "pass123")
                        .param("roleId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).create(any(UserCreateRequest.class));
    }

    @Test
    void toggleStatus_ShouldUpdateUserStatusAndRedirect() throws Exception {
        mockMvc.perform(post("/admin/users/1/status")
                        .param("status", "LOCKED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).updateStatus(eq(1L), any(UserStatusUpdateRequest.class));
    }

    @Test
    void resetPassword_ShouldResetUserPasswordAndRedirect() throws Exception {
        mockMvc.perform(post("/admin/users/1/reset-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).resetPassword(eq(1L), any(ResetPasswordRequest.class));
    }
}

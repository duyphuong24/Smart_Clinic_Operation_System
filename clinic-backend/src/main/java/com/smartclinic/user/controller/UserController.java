package com.smartclinic.user.controller;

import com.smartclinic.user.dto.ResetPasswordRequest;
import com.smartclinic.user.dto.UserCreateRequest;
import com.smartclinic.user.dto.UserStatusUpdateRequest;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.user.repository.RoleRepository;
import com.smartclinic.user.repository.UserRepository;
import com.smartclinic.user.service.UserService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    @GetMapping
    public String list(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("title", "User Management");
        return "admin/user-list";
    }

    @PostMapping("/save")
    public String saveUser(
            @RequestParam String userName,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam Long roleId
    ) {
        UserCreateRequest request = new UserCreateRequest();
        request.setUserName(userName);
        request.setFullName(fullName);
        request.setPhone(phone);
        request.setPassword(password);
        request.setRoleIds(Set.of(roleId));

        userService.create(request);
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/status")
    public String toggleStatus(@PathVariable Long id, @RequestParam UserStatus status) {
        UserStatusUpdateRequest request = new UserStatusUpdateRequest();
        request.setStatus(status);
        userService.updateStatus(id, request);
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setNewPassword("password123");
        userService.resetPassword(id, request);
        return "redirect:/admin/users";
    }
}

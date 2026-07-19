package com.smartclinic.staff.controller;

import com.smartclinic.staff.dto.StaffRequest;
import com.smartclinic.staff.dto.StaffResponse;
import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.entity.StaffType;
import com.smartclinic.staff.service.StaffService;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.repository.UserRepository;
import com.smartclinic.staff.repository.StaffRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    @GetMapping
    public String list(Model model) {
        List<StaffResponse> staffList = staffService.findAll();
        model.addAttribute("staffList", staffList);
        model.addAttribute("title", "Staff");
        return "admin/staff-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        // Only show users who do not have a staff profile
        List<User> usersWithoutStaff = userRepository.findAll().stream()
                .filter(u -> !staffRepository.existsByUserId(u.getId()))
                .toList();

        model.addAttribute("staff", new StaffRequest());
        model.addAttribute("users", usersWithoutStaff);
        model.addAttribute("staffTypes", StaffType.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("title", "Staff");
        return "admin/staff-form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("staff") StaffRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            List<User> usersWithoutStaff = userRepository.findAll().stream()
                    .filter(u -> !staffRepository.existsByUserId(u.getId()))
                    .toList();
            model.addAttribute("users", usersWithoutStaff);
            model.addAttribute("staffTypes", StaffType.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("title", "Staff");
            return "admin/staff-form";
        }
        try {
            staffService.create(request);
        } catch (Exception ex) {
            bindingResult.rejectValue("employeeCode", "error.staff", ex.getMessage());
            List<User> usersWithoutStaff = userRepository.findAll().stream()
                    .filter(u -> !staffRepository.existsByUserId(u.getId()))
                    .toList();
            model.addAttribute("users", usersWithoutStaff);
            model.addAttribute("staffTypes", StaffType.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("title", "Staff");
            return "admin/staff-form";
        }
        return "redirect:/admin/staff";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        StaffResponse staff = staffService.getById(id);
        StaffRequest request = new StaffRequest();
        request.setUserId(staff.getUserId());
        request.setEmployeeCode(staff.getEmployeeCode());
        request.setStaffType(staff.getStaffType());
        request.setHiredDate(staff.getHiredDate());
        request.setStatus(staff.getStatus());

        List<User> users = userRepository.findAll();

        model.addAttribute("staff", request);
        model.addAttribute("staffId", id);
        model.addAttribute("users", users);
        model.addAttribute("staffTypes", StaffType.values());
        model.addAttribute("staffStatuses", StaffStatus.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("title", "Staff");
        return "admin/staff-form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("staff") StaffRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            List<User> users = userRepository.findAll();
            model.addAttribute("staffId", id);
            model.addAttribute("users", users);
            model.addAttribute("staffTypes", StaffType.values());
            model.addAttribute("staffStatuses", StaffStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("title", "Staff");
            return "admin/staff-form";
        }
        try {
            staffService.update(id, request);
        } catch (Exception ex) {
            bindingResult.rejectValue("employeeCode", "error.staff", ex.getMessage());
            List<User> users = userRepository.findAll();
            model.addAttribute("staffId", id);
            model.addAttribute("users", users);
            model.addAttribute("staffTypes", StaffType.values());
            model.addAttribute("staffStatuses", StaffStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("title", "Staff");
            return "admin/staff-form";
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id) {
        staffService.deactivate(id);
        return "redirect:/admin/staff";
    }
}

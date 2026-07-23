package com.smartclinic.staff.controller;

import com.smartclinic.audit.service.AuditLogService;
import com.smartclinic.staff.entity.Staff;
import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.entity.StaffType;
import com.smartclinic.staff.repository.StaffRepository;
import com.smartclinic.user.entity.Role;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.user.repository.RoleRepository;
import com.smartclinic.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping({"/manager/staff", "/admin/staff"})
@RequiredArgsConstructor
public class StaffController {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @GetMapping
    public String listStaff(Model model) {
        List<Staff> staffList = staffRepository.findAll();
        model.addAttribute("staffList", staffList);
        model.addAttribute("staffTypes", StaffType.values());
        model.addAttribute("title", "Staff Management");
        return "manager/staff-list";
    }

    @PostMapping("/save")
    public String saveStaff(
            @RequestParam String username,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam StaffType staffType
    ) {
        if (!userRepository.existsByUserName(username)) {
            String roleName = "ROLE_" + staffType.name();
            Role role = roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));

            User user = User.builder()
                    .userName(username)
                    .passwordHash(passwordEncoder.encode("password123"))
                    .fullName(fullName)
                    .phone(phone)
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(role))
                    .build();
            User savedUser = userRepository.save(user);

            long count = staffRepository.count() + 1;
            String empCode = String.format("EMP-%06d", count);

            Staff staff = Staff.builder()
                    .user(savedUser)
                    .employeeCode(empCode)
                    .staffType(staffType)
                    .hiredDate(LocalDate.now())
                    .status(StaffStatus.ACTIVE)
                    .build();
            Staff savedStaff = staffRepository.save(staff);

            auditLogService.record("CREATE_STAFF", "STAFF", savedStaff.getId(), "Created staff member: " + fullName + " (" + empCode + ")");
        }
        return "redirect:/manager/staff";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id) {
        Staff staff = staffRepository.findById(id).orElse(null);
        if (staff != null) {
            StaffStatus newStatus = (staff.getStatus() == StaffStatus.ACTIVE) ? StaffStatus.INACTIVE : StaffStatus.ACTIVE;
            staff.setStatus(newStatus);
            staffRepository.save(staff);

            auditLogService.record("SOFT_DELETE_STAFF", "STAFF", id, "Toggled staff status to " + newStatus + " for " + staff.getEmployeeCode());
        }
        return "redirect:/manager/staff";
    }
}

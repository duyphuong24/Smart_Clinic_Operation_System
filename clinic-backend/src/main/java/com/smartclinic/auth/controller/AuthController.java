package com.smartclinic.auth.controller;

import com.smartclinic.patient.dto.PatientCreateRequest;
import com.smartclinic.patient.entity.Gender;
import com.smartclinic.patient.service.PatientService;
import com.smartclinic.report.dto.DashboardMetricsResponse;
import com.smartclinic.report.service.ReportService;
import com.smartclinic.user.entity.Role;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.user.repository.RoleRepository;
import com.smartclinic.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final ReportService reportService;
    private final PatientService patientService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("title", "Patient Online Registration");
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String dob,
            @RequestParam String gender,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (userRepository.existsByUserName(username)) {
                redirectAttributes.addFlashAttribute("error", "Username is already taken.");
                return "redirect:/register";
            }

            PatientCreateRequest request = new PatientCreateRequest();
            request.setFullName(fullName);
            request.setPhone(phone);
            request.setDateOfBirth(LocalDate.parse(dob));
            request.setGender(Gender.valueOf(gender));

            var patient = patientService.create(request);

            Role patientRole = roleRepository.findByName("ROLE_PATIENT")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_PATIENT").build()));

            User user = User.builder()
                    .userName(username)
                    .passwordHash(passwordEncoder.encode(password))
                    .fullName(fullName)
                    .phone(phone)
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(patientRole))
                    .build();
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success", "Account registered successfully! Please log in.");
            return "redirect:/login";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + ex.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"))) {
            return "redirect:/my-portal";
        }

        DashboardMetricsResponse metrics = reportService.dashboardMetrics();

        model.addAttribute("appointmentsCount", metrics.getTodayAppointments());
        model.addAttribute("waitingQueueCount", metrics.getActiveQueueItems());
        model.addAttribute("completedVisitsCount", metrics.getCompletedVisits());
        model.addAttribute("totalRevenue", metrics.getTodayRevenue() != null ? metrics.getTodayRevenue() : BigDecimal.ZERO);
        model.addAttribute("title", "Dashboard");

        return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}

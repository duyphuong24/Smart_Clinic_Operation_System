package com.smartclinic.auth.controller;

import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.encounter.serviceorder.repository.EncounterServiceOrderRepository;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrderStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AppointmentRepository appointmentRepository;
    private final QueueItemRepository queueItemRepository;
    private final VisitRepository visitRepository;
    private final EncounterServiceOrderRepository encounterServiceOrderRepository;

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);

        long appointmentsCount = appointmentRepository.findAll().stream()
                .filter(a -> a.getScheduledStart().isAfter(todayStart) && a.getScheduledStart().isBefore(todayEnd))
                .count();

        long waitingQueueCount = queueItemRepository.findAll().stream()
                .filter(q -> q.getQueueDate().equals(today) && q.getStatus() == com.smartclinic.queue.entity.QueueStatus.WAITING)
                .count();

        long completedVisitsCount = visitRepository.findAll().stream()
                .filter(v -> v.getCreatedAt().isAfter(todayStart) && v.getCreatedAt().isBefore(todayEnd) && v.getStatus() == com.smartclinic.visit.entity.VisitStatus.COMPLETED)
                .count();

        // Calculate Revenue: Doctor Consultation Fee + Service Catalog Fees
        BigDecimal doctorFees = visitRepository.findAll().stream()
                .filter(v -> v.getCreatedAt().isAfter(todayStart) && v.getCreatedAt().isBefore(todayEnd) && v.getStatus() == com.smartclinic.visit.entity.VisitStatus.COMPLETED)
                .map(v -> v.getDoctor().getConsultationFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal serviceFees = encounterServiceOrderRepository.findAll().stream()
                .filter(es -> es.getCreatedAt().isAfter(todayStart) && es.getCreatedAt().isBefore(todayEnd) && es.getStatus() == EncounterServiceOrderStatus.ORDERED)
                .map(es -> es.getUnitPrice().multiply(BigDecimal.valueOf(es.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRevenue = doctorFees.add(serviceFees);

        model.addAttribute("appointmentsCount", appointmentsCount);
        model.addAttribute("waitingQueueCount", waitingQueueCount);
        model.addAttribute("completedVisitsCount", completedVisitsCount);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("title", "Dashboard");

        return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}

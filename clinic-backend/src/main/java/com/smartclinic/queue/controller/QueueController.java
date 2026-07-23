package com.smartclinic.queue.controller;

import com.smartclinic.audit.service.AuditLogService;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.encounter.dto.EncounterCreateRequest;
import com.smartclinic.encounter.dto.EncounterResponse;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.encounter.service.EncounterWorkflowService;
import com.smartclinic.masterdata.service.RoomService;
import com.smartclinic.patient.service.PatientService;
import com.smartclinic.queue.dto.QueueItemResponse;
import com.smartclinic.queue.dto.WalkInQueueRequest;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.entity.QueuePriority;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.queue.service.QueueItemService;
import com.smartclinic.visit.dto.VisitCreateRequest;
import com.smartclinic.visit.entity.Visit;
import com.smartclinic.visit.repository.VisitRepository;
import com.smartclinic.visit.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueItemService queueItemService;
    private final DoctorService doctorService;
    private final RoomService roomService;
    private final PatientService patientService;
    private final DoctorRepository doctorRepository;
    private final QueueItemRepository queueItemRepository;
    private final VisitRepository visitRepository;
    private final VisitService visitService;
    private final EncounterRepository encounterRepository;
    private final EncounterWorkflowService encounterWorkflowService;
    private final AuditLogService auditLogService;

    @GetMapping
    public String board(
            @RequestParam(required = false) Long doctorId,
            Model model
    ) {
        List<QueueItemResponse> queueItems = queueItemService.findActive(LocalDate.now(), doctorId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Doctor currentDoctor = null;
        if (auth != null && auth.isAuthenticated()) {
            currentDoctor = doctorRepository.findByStaffUserUserName(auth.getName()).orElse(null);
        }

        model.addAttribute("queueItems", queueItems);
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("patients", patientService.search("", PageRequest.of(0, 100)).getItems());
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("currentDoctor", currentDoctor);
        model.addAttribute("walkIn", new WalkInQueueRequest());
        model.addAttribute("priorities", QueuePriority.values());
        model.addAttribute("title", "Queue Board");
        return "queue/board";
    }

    @PostMapping("/walk-in")
    public String createWalkIn(
            @Valid @ModelAttribute("walkIn") WalkInQueueRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            List<QueueItemResponse> queueItems = queueItemService.findActive(LocalDate.now(), null);
            model.addAttribute("queueItems", queueItems);
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("patients", patientService.search("", PageRequest.of(0, 100)).getItems());
            model.addAttribute("priorities", QueuePriority.values());
            model.addAttribute("title", "Queue Board");
            return "queue/board";
        }
        queueItemService.createWalkIn(request);
        return "redirect:/queue";
    }

    @PostMapping("/{id}/call")
    public String callPatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        QueueItem item = queueItemRepository.findById(id).orElse(null);
        if (item != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
                String username = auth.getName();
                Doctor doctor = doctorRepository.findByStaffUserUserName(username).orElse(null);
                if (doctor != null) {
                    if (item.getDoctor() != null && !item.getDoctor().getId().equals(doctor.getId())) {
                        redirectAttributes.addFlashAttribute("error", "You can only call your assigned patients.");
                        return "redirect:/queue";
                    }
                    if (item.getDoctor() == null) {
                        item.setDoctor(doctor);
                        queueItemRepository.save(item);
                    }
                }
            }
        }
        queueItemService.call(id);
        return "redirect:/queue";
    }

    @PostMapping("/{id}/start")
    public String startService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            QueueItem queueItem = queueItemRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Queue Item ID"));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Doctor loggedInDoctor = null;
            if (auth != null && auth.isAuthenticated()) {
                String username = auth.getName();
                loggedInDoctor = doctorRepository.findByStaffUserUserName(username).orElse(null);
                if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
                    if (loggedInDoctor != null && queueItem.getDoctor() != null && !queueItem.getDoctor().getId().equals(loggedInDoctor.getId())) {
                        redirectAttributes.addFlashAttribute("error", "You can only start consultation for your assigned patients.");
                        return "redirect:/queue";
                    }
                }
            }

            if (queueItem.getDoctor() == null) {
                if (loggedInDoctor == null) {
                    loggedInDoctor = doctorRepository.findAll().stream()
                            .filter(Doctor::isActive)
                            .findFirst()
                            .orElse(null);
                }
                if (loggedInDoctor != null) {
                    queueItem.setDoctor(loggedInDoctor);
                    queueItemRepository.save(queueItem);
                }
            }

            if (queueItem.getStatus() == QueueStatus.CALLED || queueItem.getStatus() == QueueStatus.WAITING) {
                queueItemService.startService(id);
            }

            Visit visit = visitRepository.findByQueueItemId(id)
                    .orElseGet(() -> {
                        VisitCreateRequest request = new VisitCreateRequest();
                        request.setQueueItemId(id);
                        Long visitId = visitService.create(request).getId();
                        return visitRepository.findById(visitId)
                                .orElseThrow(() -> new IllegalStateException("Visit was not created"));
                    });

            Encounter encounter = encounterRepository.findByVisitId(visit.getId())
                    .orElseGet(() -> {
                        EncounterCreateRequest request = new EncounterCreateRequest();
                        request.setVisitId(visit.getId());
                        EncounterResponse response = encounterWorkflowService.create(request);
                        return encounterRepository.findById(response.getId())
                                .orElseThrow(() -> new IllegalStateException("Encounter was not created"));
                    });

            auditLogService.record("START_CONSULTATION", "ENCOUNTER", encounter.getId(), "Started consultation for patient: " + visit.getPatient().getFullName());
            return "redirect:/consultation/encounters/" + encounter.getId();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/queue";
        }
    }

    @PostMapping("/{id}/skip")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_RECEPTIONIST')")
    public String skipPatient(@PathVariable Long id) {
        queueItemService.skip(id);
        return "redirect:/queue";
    }

    @PostMapping("/{id}/done")
    public String completeService(@PathVariable Long id) {
        queueItemService.done(id);
        return "redirect:/queue";
    }
}

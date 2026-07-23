package com.smartclinic.encounter.controller;

import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.encounter.dto.EncounterCreateRequest;
import com.smartclinic.encounter.dto.EncounterResponse;
import com.smartclinic.encounter.dto.EncounterUpdateRequest;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.entity.EncounterStatus;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.encounter.serviceorder.entity.EncounterServiceOrder;
import com.smartclinic.encounter.serviceorder.repository.EncounterServiceOrderRepository;
import com.smartclinic.encounter.serviceorder.dto.EncounterServiceOrderRequest;
import com.smartclinic.encounter.serviceorder.service.EncounterServiceOrderService;
import com.smartclinic.encounter.service.EncounterWorkflowService;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.queue.service.QueueItemService;
import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.repository.ServiceCatalogRepository;
import com.smartclinic.visit.dto.VisitCreateRequest;
import com.smartclinic.visit.entity.Visit;
import com.smartclinic.visit.repository.VisitRepository;
import com.smartclinic.visit.service.VisitService;
import com.smartclinic.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/consultation")
@RequiredArgsConstructor
public class ConsultationController {

    private final DoctorRepository doctorRepository;
    private final QueueItemRepository queueItemRepository;
    private final QueueItemService queueItemService;
    private final VisitRepository visitRepository;
    private final EncounterRepository encounterRepository;
    private final EncounterServiceOrderRepository encounterServiceOrderRepository;
    private final EncounterServiceOrderService encounterServiceOrderService;
    private final ServiceCatalogRepository serviceCatalogRepository;
    private final VisitService visitService;
    private final EncounterWorkflowService encounterWorkflowService;
    private final AuditLogService auditLogService;

    @GetMapping
    public String doctorQueue(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdminOrManager = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));

        Doctor doctor = doctorRepository.findByStaffUserUserName(username).orElse(null);

        if (doctor == null) {
            doctor = doctorRepository.findAll().stream()
                    .filter(Doctor::isActive)
                    .findFirst()
                    .orElse(null);
        }

        List<QueueItem> queueItems = List.of();
        if (isAdminOrManager && doctor == null) {
            queueItems = queueItemRepository.findByQueueDateAndStatusNotInOrderByQueueNumberAsc(
                    LocalDate.now(),
                    List.of(QueueStatus.DONE, QueueStatus.SKIPPED)
            );
            if (queueItems.isEmpty()) {
                queueItems = queueItemRepository.findAll().stream()
                        .filter(q -> q.getStatus() != QueueStatus.DONE && q.getStatus() != QueueStatus.SKIPPED)
                        .toList();
            }
        } else if (doctor != null) {
            final Long targetDoctorId = doctor.getId();
            queueItems = queueItemRepository.findActiveByDateAndDoctor(
                    LocalDate.now(),
                    targetDoctorId,
                    List.of(QueueStatus.DONE, QueueStatus.SKIPPED)
            );

            if (queueItems.isEmpty()) {
                queueItems = queueItemRepository.findAll().stream()
                        .filter(q -> (q.getDoctor() == null || q.getDoctor().getId().equals(targetDoctorId))
                                && q.getStatus() != QueueStatus.DONE && q.getStatus() != QueueStatus.SKIPPED)
                        .toList();
            }
        } else {
            model.addAttribute("info", "No active doctors currently configured in the system.");
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("queueItems", queueItems);
        model.addAttribute("title", "Doctor Patient Queue");
        return "encounter/queue";
    }

    @PostMapping("/start")
    public String startConsultation(@RequestParam Long queueItemId) {
        QueueItem queueItem = queueItemRepository.findById(queueItemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Queue Item ID"));

        if (queueItem.getDoctor() == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "";
            Doctor loggedInDoctor = doctorRepository.findByStaffUserUserName(username).orElse(null);
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
            queueItemService.startService(queueItemId);
        }

        Visit visit = visitRepository.findByQueueItemId(queueItemId)
                .orElseGet(() -> {
                    VisitCreateRequest request = new VisitCreateRequest();
                    request.setQueueItemId(queueItemId);
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
    }

    @GetMapping("/encounters/{id}")
    public String encounterForm(@PathVariable Long id, Model model) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Encounter ID"));

        if (encounter.getStatus() == EncounterStatus.COMPLETED) {
            return "redirect:/consultation/encounters/" + id + "/detail";
        }

        List<EncounterServiceOrder> orderedServices = encounterServiceOrderRepository.findByEncounterId(id);
        List<ServiceCatalog> availableServices = serviceCatalogRepository.findByActiveTrue();

        model.addAttribute("encounter", encounter);
        model.addAttribute("patient", encounter.getVisit().getPatient());
        model.addAttribute("orderedServices", orderedServices);
        model.addAttribute("availableServices", availableServices);
        model.addAttribute("title", "Consultation Desk");
        return "encounter/form";
    }

    @PostMapping("/encounters/{id}/services")
    public String addService(
            @PathVariable Long id,
            @RequestParam Long serviceId,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(defaultValue = "") String note,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes
    ) {
        try {
            EncounterServiceOrderRequest request = new EncounterServiceOrderRequest();
            request.setServiceCatalogId(serviceId);
            request.setQuantity(quantity);
            request.setNote(note);

            encounterServiceOrderService.add(id, request);
            auditLogService.record("ORDER_SERVICE", "ENCOUNTER_SERVICE_ORDER", id, "Doctor ordered medical service ID: " + serviceId + " for encounter " + id);
            redirectAttributes.addFlashAttribute("success", "Service ordered successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/consultation/encounters/" + id;
    }

    @PostMapping("/encounters/{id}/complete")
    public String completeConsultation(
            @PathVariable Long id,
            @ModelAttribute("encounter") Encounter encounterData,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes
    ) {
        try {
            EncounterUpdateRequest request = new EncounterUpdateRequest();
            request.setChiefComplaint(encounterData.getChiefComplaint());
            request.setDiagnosis(encounterData.getDiagnosis());
            request.setClinicalNote(encounterData.getClinicalNote());
            encounterWorkflowService.update(id, request);
            encounterWorkflowService.complete(id);
            auditLogService.record("COMPLETE_CONSULTATION", "ENCOUNTER", id, "Completed consultation encounter ID: " + id);
            redirectAttributes.addFlashAttribute("success", "Consultation completed successfully.");
            return "redirect:/consultation/encounters/" + id + "/detail";
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/consultation/encounters/" + id;
        }
    }

    @GetMapping("/encounters/{id}/detail")
    public String detail(@PathVariable Long id, Model model) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Encounter ID"));

        List<EncounterServiceOrder> orderedServices = encounterServiceOrderRepository.findByEncounterId(id);

        model.addAttribute("encounter", encounter);
        model.addAttribute("patient", encounter.getVisit().getPatient());
        model.addAttribute("orderedServices", orderedServices);
        model.addAttribute("title", "Consultation Detail");
        return "encounter/detail";
    }
}
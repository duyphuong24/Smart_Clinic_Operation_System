package com.smartclinic.encounter.controller;

import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.entity.EncounterService;
import com.smartclinic.encounter.entity.EncounterServiceStatus;
import com.smartclinic.encounter.entity.EncounterStatus;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.encounter.repository.EncounterServiceRepository;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.queue.service.QueueItemService;
import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.repository.ServiceCatalogRepository;
import com.smartclinic.visit.entity.Visit;
import com.smartclinic.visit.entity.VisitStatus;
import com.smartclinic.visit.repository.VisitRepository;
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
import java.time.LocalDateTime;
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
    private final EncounterServiceRepository encounterServiceRepository;
    private final ServiceCatalogRepository serviceCatalogRepository;

    @GetMapping
    public String doctorQueue(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Doctor doctor = doctorRepository.findByStaffUserUserName(username).orElse(null);

        if (doctor == null) {
            model.addAttribute("error", "Your user account is not linked to any active Doctor profile.");
            model.addAttribute("title", "Consultation Queue");
            return "encounter/queue";
        }

        List<QueueItem> queueItems = queueItemRepository.findActiveByDateAndDoctor(
                LocalDate.now(),
                doctor.getId(),
                List.of(com.smartclinic.queue.entity.QueueStatus.DONE, com.smartclinic.queue.entity.QueueStatus.SKIPPED)
        );

        model.addAttribute("doctor", doctor);
        model.addAttribute("queueItems", queueItems);
        model.addAttribute("title", "Consultation Queue");
        return "encounter/queue";
    }

    @PostMapping("/start")
    public String startConsultation(@RequestParam Long queueItemId) {
        QueueItem queueItem = queueItemRepository.findById(queueItemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Queue Item ID"));

        // Transition queue status to IN_SERVICE if it is not already
        if (queueItem.getStatus() != com.smartclinic.queue.entity.QueueStatus.IN_SERVICE) {
            queueItemService.startService(queueItemId);
        }

        // Get or Create Visit
        Visit visit = visitRepository.findByQueueItemId(queueItemId).orElseGet(() -> {
            Visit newVisit = new Visit();
            newVisit.setVisitCode("VIS-" + System.currentTimeMillis());
            newVisit.setPatient(queueItem.getPatient());
            newVisit.setDoctor(queueItem.getDoctor());
            newVisit.setAppointment(queueItem.getAppointment());
            newVisit.setQueueItem(queueItem);
            newVisit.setStatus(VisitStatus.IN_CONSULTATION);
            newVisit.setStartedAt(LocalDateTime.now());
            return visitRepository.save(newVisit);
        });

        // Get or Create Encounter
        Encounter encounter = encounterRepository.findByVisitId(visit.getId()).orElseGet(() -> {
            Encounter newEncounter = new Encounter();
            newEncounter.setVisit(visit);
            newEncounter.setDoctor(visit.getDoctor());
            newEncounter.setStatus(EncounterStatus.OPEN);
            newEncounter.setStartedAt(LocalDateTime.now());
            return encounterRepository.save(newEncounter);
        });

        return "redirect:/consultation/encounters/" + encounter.getId();
    }

    @GetMapping("/encounters/{id}")
    public String encounterForm(@PathVariable Long id, Model model) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Encounter ID"));

        if (encounter.getStatus() == EncounterStatus.COMPLETED) {
            return "redirect:/consultation/encounters/" + id + "/detail";
        }

        List<EncounterService> orderedServices = encounterServiceRepository.findByEncounterId(id);
        List<ServiceCatalog> availableServices = serviceCatalogRepository.findByActiveTrue();

        model.addAttribute("encounter", encounter);
        model.addAttribute("patient", encounter.getVisit().getPatient());
        model.addAttribute("orderedServices", orderedServices);
        model.addAttribute("availableServices", availableServices);
        model.addAttribute("title", "Consultation");
        return "encounter/form";
    }

    @PostMapping("/encounters/{id}/services")
    public String addService(
            @PathVariable Long id,
            @RequestParam Long serviceId,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(defaultValue = "") String note
    ) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Encounter ID"));

        ServiceCatalog service = serviceCatalogRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Service Catalog ID"));

        EncounterService order = new EncounterService();
        order.setEncounter(encounter);
        order.setServiceCatalog(service);
        order.setQuantity(quantity);
        order.setUnitPrice(service.getPrice());
        order.setNote(note);
        order.setStatus(EncounterServiceStatus.ORDERED);
        encounterServiceRepository.save(order);

        return "redirect:/consultation/encounters/" + id;
    }

    @PostMapping("/encounters/{id}/complete")
    public String completeConsultation(
            @PathVariable Long id,
            @ModelAttribute("encounter") Encounter encounterData
    ) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Encounter ID"));

        // Save diagnostic data
        encounter.setChiefComplaint(encounterData.getChiefComplaint());
        encounter.setDiagnosis(encounterData.getDiagnosis());
        encounter.setClinicalNote(encounterData.getClinicalNote());
        encounter.setStatus(EncounterStatus.COMPLETED);
        encounter.setCompletedAt(LocalDateTime.now());
        encounterRepository.save(encounter);

        // Update Visit Status
        Visit visit = encounter.getVisit();
        visit.setStatus(VisitStatus.COMPLETED);
        visit.setEndedAt(LocalDateTime.now());
        visitRepository.save(visit);

        // Update Queue Status
        if (visit.getQueueItem() != null) {
            queueItemService.done(visit.getQueueItem().getId());
        }

        return "redirect:/consultation/encounters/" + id + "/detail";
    }

    @GetMapping("/encounters/{id}/detail")
    public String detail(@PathVariable Long id, Model model) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Encounter ID"));

        List<EncounterService> orderedServices = encounterServiceRepository.findByEncounterId(id);

        model.addAttribute("encounter", encounter);
        model.addAttribute("patient", encounter.getVisit().getPatient());
        model.addAttribute("orderedServices", orderedServices);
        model.addAttribute("title", "Consultation Detail");
        return "encounter/detail";
    }
}

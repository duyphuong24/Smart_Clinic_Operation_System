package com.smartclinic.queue.controller;

import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.masterdata.service.RoomService;
import com.smartclinic.patient.service.PatientService;
import com.smartclinic.queue.dto.QueueItemResponse;
import com.smartclinic.queue.dto.WalkInQueueRequest;
import com.smartclinic.queue.entity.QueuePriority;
import com.smartclinic.queue.service.QueueItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping
    public String board(
            @RequestParam(required = false) Long doctorId,
            Model model
    ) {
        List<QueueItemResponse> queueItems = queueItemService.findActive(LocalDate.now(), doctorId);

        model.addAttribute("queueItems", queueItems);
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("patients", patientService.search("", PageRequest.of(0, 100)).getItems());
        model.addAttribute("selectedDoctorId", doctorId);
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
    public String callPatient(@PathVariable Long id) {
        queueItemService.call(id);
        return "redirect:/queue";
    }

    @PostMapping("/{id}/start")
    public String startService(@PathVariable Long id) {
        queueItemService.startService(id);
        return "redirect:/queue";
    }

    @PostMapping("/{id}/skip")
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

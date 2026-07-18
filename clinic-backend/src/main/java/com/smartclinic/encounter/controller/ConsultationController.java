package com.smartclinic.encounter.controller;

import com.smartclinic.queue.dto.QueueItemResponse;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.service.QueueItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/consultation")
@RequiredArgsConstructor
public class ConsultationController {

    private final QueueItemService queueItemService;

    @GetMapping
    public String index(Model model) {
        List<QueueItemResponse> inServiceItems = queueItemService.findActive(LocalDate.now(), null).stream()
                .filter(item -> item.getStatus() == QueueStatus.IN_SERVICE)
                .toList();

        model.addAttribute("queueItems", inServiceItems);
        model.addAttribute("title", "Consultation");
        return "encounter/consultation";
    }
}
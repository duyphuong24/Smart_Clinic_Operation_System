package com.smartclinic.doctor.controller;

import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.entity.QueueStatus;
import com.smartclinic.queue.repository.QueueItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorPortalController {

    private final DoctorRepository doctorRepository;
    private final QueueItemRepository queueItemRepository;

    @GetMapping("/my-patients")
    public String myPatients(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "";

        Doctor doctor = doctorRepository.findByStaffUserUserName(username).orElse(null);

        if (doctor == null) {
            doctor = doctorRepository.findAll().stream()
                    .filter(Doctor::isActive)
                    .findFirst()
                    .orElse(null);
        }

        List<QueueItem> queueItems = List.of();
        if (doctor != null && doctor.getId() != null) {
            final Long targetDoctorId = doctor.getId();
            queueItems = queueItemRepository.findAll().stream()
                    .filter(q -> q != null && q.getDoctor() != null && q.getDoctor().getId() != null && q.getDoctor().getId().equals(targetDoctorId))
                    .filter(q -> q.getQueueDate() != null && q.getQueueDate().equals(LocalDate.now()))
                    .toList();

            if (queueItems.isEmpty()) {
                queueItems = queueItemRepository.findAll().stream()
                        .filter(q -> q != null && q.getDoctor() != null && q.getDoctor().getId() != null && q.getDoctor().getId().equals(targetDoctorId))
                        .toList();
            }
        }

        long waitingCount = queueItems.stream()
                .filter(q -> q.getStatus() == QueueStatus.WAITING || q.getStatus() == QueueStatus.CALLED)
                .count();
        long inServiceCount = queueItems.stream()
                .filter(q -> q.getStatus() == QueueStatus.IN_SERVICE)
                .count();
        long doneCount = queueItems.stream()
                .filter(q -> q.getStatus() == QueueStatus.DONE)
                .count();

        model.addAttribute("doctor", doctor);
        model.addAttribute("queueItems", queueItems);
        model.addAttribute("waitingCount", waitingCount);
        model.addAttribute("inServiceCount", inServiceCount);
        model.addAttribute("doneCount", doneCount);
        model.addAttribute("totalCount", queueItems.size());
        model.addAttribute("title", "Today's Patients");
        return "doctor/my-patients";
    }
}

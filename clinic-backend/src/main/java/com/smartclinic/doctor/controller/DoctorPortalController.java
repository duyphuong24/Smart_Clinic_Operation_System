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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
            queueItems = queueItemRepository.findByQueueDateAndDoctorIdOrderByQueueNumberAsc(LocalDate.now(), doctor.getId());
            if (queueItems.isEmpty()) {
                queueItems = queueItemRepository.findByDoctorIdOrderByQueueNumberAsc(doctor.getId());
            }
        }

        long waitingCount = queueItems.stream()
                .filter(q -> q != null && (q.getStatus() == QueueStatus.WAITING || q.getStatus() == QueueStatus.CALLED))
                .count();
        long inServiceCount = queueItems.stream()
                .filter(q -> q != null && q.getStatus() == QueueStatus.IN_SERVICE)
                .count();
        long doneCount = queueItems.stream()
                .filter(q -> q != null && q.getStatus() == QueueStatus.DONE)
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

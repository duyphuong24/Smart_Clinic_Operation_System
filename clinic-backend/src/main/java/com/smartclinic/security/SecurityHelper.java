package com.smartclinic.security;

import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.queue.entity.QueueItem;
import com.smartclinic.queue.repository.QueueItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("securityHelper")
@RequiredArgsConstructor
public class SecurityHelper {

    private final DoctorRepository doctorRepository;
    private final EncounterRepository encounterRepository;
    private final QueueItemRepository queueItemRepository;

    public boolean isEncounterOwner(Long encounterId) {
        String username = getCurrentUsername();
        if (username == null) {
            return false;
        }

        if (hasRole("ROLE_ADMIN")) {
            return true;
        }

        Encounter encounter = encounterRepository.findById(encounterId).orElse(null);
        if (encounter == null || encounter.getDoctor() == null) {
            return false;
        }

        Doctor doctor = getDoctorForUsername(username);
        return doctor != null && doctor.getId().equals(encounter.getDoctor().getId());
    }

    public boolean isQueueItemOwner(Long queueItemId) {
        String username = getCurrentUsername();
        if (username == null) {
            return false;
        }

        if (hasRole("ROLE_ADMIN")) {
            return true;
        }

        QueueItem queueItem = queueItemRepository.findById(queueItemId).orElse(null);
        if (queueItem == null || queueItem.getDoctor() == null) {
            return false;
        }

        Doctor doctor = getDoctorForUsername(username);
        return doctor != null && doctor.getId().equals(queueItem.getDoctor().getId());
    }

    private String getCurrentUsername() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }

    private boolean hasRole(String roleName) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return false;
        }
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(roleName));
    }

    private Doctor getDoctorForUsername(String username) {
        return doctorRepository.findByStaffUserUserName(username).orElse(null);
    }
}

package com.smartclinic.config;

import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.entity.ServiceType;
import com.smartclinic.servicecatalog.repository.ServiceCatalogRepository;
import com.smartclinic.user.entity.Role;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.user.repository.RoleRepository;
import com.smartclinic.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "smartclinic.seed-demo-data", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class DemoDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceCatalogRepository serviceCatalogRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Role admin = ensureRole("ADMIN");
        Role receptionist = ensureRole("RECEPTIONIST");
        Role doctor = ensureRole("DOCTOR");
        Role cashier = ensureRole("CASHIER");
        Role manager = ensureRole("MANAGER");

        ensureUser("admin", "admin123", "System Admin", admin);
        ensureUser("receptionist", "receptionist123", "Demo Receptionist", receptionist);
        ensureUser("doctor", "doctor123", "Demo Doctor", doctor);
        ensureUser("cashier", "cashier123", "Demo Cashier", cashier);
        ensureUser("manager", "manager123", "Demo Manager", manager);

        ensureService("SVC-CONS", "General Consultation", ServiceType.CONSULTATION, new BigDecimal("150000"));
        ensureService("SVC-XPUL", "Pulmonary X-Ray", ServiceType.PROCEDURE, new BigDecimal("250000"));
        ensureService("SVC-CBC", "Complete Blood Count Test", ServiceType.LAB_TEST, new BigDecimal("100000"));
        ensureService("SVC-HECG", "Electrocardiogram (ECG)", ServiceType.PROCEDURE, new BigDecimal("200000"));
    }

    private Role ensureRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(name)
                        .build()));
    }

    private void ensureUser(String userName, String rawPassword, String fullName, Role role) {
        if (userRepository.existsByUserName(userName)) {
            return;
        }

        User user = User.builder()
                .userName(userName)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .fullName(fullName)
                .status(UserStatus.ACTIVE)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);
    }

    private void ensureService(String code, String name, ServiceType type, BigDecimal price) {
        if (serviceCatalogRepository.findByServiceCodeIgnoreCase(code).isPresent()) {
            return;
        }

        ServiceCatalog service = new ServiceCatalog();
        service.setServiceCode(code);
        service.setName(name);
        service.setType(type);
        service.setPrice(price);
        service.setActive(true);
        serviceCatalogRepository.save(service);
    }
}

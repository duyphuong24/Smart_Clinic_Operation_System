package com.smartclinic.config;

import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.entity.ServiceType;
import com.smartclinic.servicecatalog.repository.ServiceCatalogRepository;
import com.smartclinic.user.entity.Role;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.user.repository.RoleRepository;
import com.smartclinic.user.repository.UserRepository;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.entity.Specialty;
import com.smartclinic.masterdata.repository.RoomRepository;
import com.smartclinic.masterdata.repository.SpecialtyRepository;
import com.smartclinic.staff.entity.Staff;
import com.smartclinic.staff.entity.StaffType;
import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.repository.StaffRepository;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final RoomRepository roomRepository;
    private final SpecialtyRepository specialtyRepository;
    private final StaffRepository staffRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = ensureRole("ADMIN");
        Role receptionistRole = ensureRole("RECEPTIONIST");
        Role doctorRole = ensureRole("DOCTOR");
        Role cashierRole = ensureRole("CASHIER");
        Role managerRole = ensureRole("MANAGER");

        User adminUser = ensureUser("admin", "admin123", "System Admin", adminRole);
        User receptionistUser = ensureUser("receptionist", "receptionist123", "Demo Receptionist", receptionistRole);
        User doctorUser = ensureUser("doctor", "doctor123", "Demo Doctor", doctorRole);
        User cashierUser = ensureUser("cashier", "cashier123", "Demo Cashier", cashierRole);
        User managerUser = ensureUser("manager", "manager123", "Demo Manager", managerRole);

        Room room101 = ensureRoom("ROOM-101", "Room 101", "1st Floor");
        ensureRoom("ROOM-102", "Room 102", "1st Floor");
        ensureRoom("ROOM-103", "Room 103", "2nd Floor");

        Specialty specialtyGen = ensureSpecialty("General Medicine", "General health check-up and consulting");
        ensureSpecialty("Cardiology", "Heart diseases and cardiovascular healthcare");

        ensureService("SVC-CONS", "General Consultation", ServiceType.CONSULTATION, new BigDecimal("150000"));
        ensureService("SVC-XPUL", "Pulmonary X-Ray", ServiceType.PROCEDURE, new BigDecimal("250000"));
        ensureService("SVC-CBC", "Complete Blood Count Test", ServiceType.LAB_TEST, new BigDecimal("100000"));
        ensureService("SVC-HECG", "Electrocardiogram (ECG)", ServiceType.PROCEDURE, new BigDecimal("200000"));

        ensureStaff(adminUser, "EMP-001", StaffType.ADMINISTRATOR);
        ensureStaff(receptionistUser, "EMP-002", StaffType.RECEPTIONIST);
        Staff docStaff = ensureStaff(doctorUser, "EMP-003", StaffType.DOCTOR);
        ensureStaff(cashierUser, "EMP-004", StaffType.CASHIER);
        ensureStaff(managerUser, "EMP-005", StaffType.MANAGER);

        if (docStaff != null) {
            ensureDoctor(docStaff, specialtyGen, room101, "LIC-99999", new BigDecimal("150000"));
        }
    }

    private Role ensureRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(name)
                        .build()));
    }

    private User ensureUser(String userName, String rawPassword, String fullName, Role role) {
        return userRepository.findByUserName(userName)
                .orElseGet(() -> userRepository.save(User.builder()
                        .userName(userName)
                        .passwordHash(passwordEncoder.encode(rawPassword))
                        .fullName(fullName)
                        .status(UserStatus.ACTIVE)
                        .roles(Set.of(role))
                        .build()));
    }

    private Room ensureRoom(String code, String name, String floor) {
        return roomRepository.findByRoomCodeIgnoreCase(code)
                .orElseGet(() -> {
                    if (roomRepository.existsByRoomCodeIgnoreCase(code)) {
                        return null;
                    }
                    return roomRepository.save(Room.builder()
                            .roomCode(code)
                            .name(name)
                            .floor(floor)
                            .active(true)
                            .build());
                });
    }

    private Specialty ensureSpecialty(String name, String description) {
        return specialtyRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    if (specialtyRepository.existsByNameIgnoreCase(name)) {
                        return null;
                    }
                    return specialtyRepository.save(Specialty.builder()
                            .name(name)
                            .description(description)
                            .active(true)
                            .build());
                });
    }

    private Staff ensureStaff(User user, String employeeCode, StaffType type) {
        // Avoid duplicate user id mapping
        java.util.Optional<Staff> existingByUser = staffRepository.findByUserId(user.getId());
        if (existingByUser.isPresent()) {
            return existingByUser.get();
        }

        // Avoid duplicate employee code mapping
        java.util.Optional<Staff> existingByCode = staffRepository.findByEmployeeCodeIgnoreCase(employeeCode);
        if (existingByCode.isPresent()) {
            return existingByCode.get();
        }

        return staffRepository.save(Staff.builder()
                .user(user)
                .employeeCode(employeeCode)
                .staffType(type)
                .status(StaffStatus.ACTIVE)
                .hiredDate(LocalDate.now())
                .build());
    }

    private void ensureDoctor(Staff staff, Specialty specialty, Room defaultRoom, String licenseNo, BigDecimal fee) {
        if (doctorRepository.findByStaffUserUserName(staff.getUser().getUserName()).isPresent()) {
            return;
        }
        if (doctorRepository.existsByLicenseNoIgnoreCase(licenseNo)) {
            return;
        }
        doctorRepository.save(Doctor.builder()
                .staff(staff)
                .specialty(specialty)
                .defaultRoom(defaultRoom)
                .licenseNo(licenseNo)
                .consultationFee(fee)
                .bio("Seeded demo doctor for system testing.")
                .active(true)
                .build());
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

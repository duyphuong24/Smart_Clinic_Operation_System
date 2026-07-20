package com.smartclinic.config;

import com.smartclinic.appointment.entity.Appointment;
import com.smartclinic.appointment.entity.AppointmentSource;
import com.smartclinic.appointment.entity.AppointmentStatus;
import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.doctor.entity.Doctor;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.entity.Specialty;
import com.smartclinic.masterdata.repository.RoomRepository;
import com.smartclinic.masterdata.repository.SpecialtyRepository;
import com.smartclinic.patient.entity.Gender;
import com.smartclinic.patient.entity.Patient;
import com.smartclinic.patient.entity.PatientStatus;
import com.smartclinic.patient.repository.PatientRepository;
import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.entity.ServiceType;
import com.smartclinic.servicecatalog.repository.ServiceCatalogRepository;
import com.smartclinic.staff.entity.Staff;
import com.smartclinic.staff.entity.StaffStatus;
import com.smartclinic.staff.entity.StaffType;
import com.smartclinic.staff.repository.StaffRepository;
import com.smartclinic.user.entity.Role;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.user.repository.RoleRepository;
import com.smartclinic.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

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
        Room room102 = ensureRoom("ROOM-102", "Room 102", "1st Floor");
        ensureRoom("ROOM-103", "Room 103", "2nd Floor");

        Specialty specialtyGen = ensureSpecialty("General Medicine", "General health check-up and consulting");
        Specialty specialtyCardio = ensureSpecialty("Cardiology", "Heart diseases and cardiovascular healthcare");

        ensureService("SVC-CONS", "General Consultation", ServiceType.CONSULTATION, new BigDecimal("150000"));
        ensureService("SVC-XPUL", "Pulmonary X-Ray", ServiceType.PROCEDURE, new BigDecimal("250000"));
        ensureService("SVC-CBC", "Complete Blood Count Test", ServiceType.LAB_TEST, new BigDecimal("100000"));
        ensureService("SVC-HECG", "Electrocardiogram (ECG)", ServiceType.PROCEDURE, new BigDecimal("200000"));

        ensureStaff(adminUser, "EMP-001", StaffType.ADMINISTRATOR);
        ensureStaff(receptionistUser, "EMP-002", StaffType.RECEPTIONIST);
        Staff docStaff = ensureStaff(doctorUser, "EMP-003", StaffType.DOCTOR);
        ensureStaff(cashierUser, "EMP-004", StaffType.CASHIER);
        ensureStaff(managerUser, "EMP-005", StaffType.MANAGER);

        Doctor demoDoctor = null;
        if (docStaff != null) {
            demoDoctor = ensureDoctor(docStaff, specialtyGen, room101, "LIC-99999", new BigDecimal("150000"));
        }

        Patient p1 = ensurePatient("BN-20260701-0001", "Nguyen Van An", "0905111222", LocalDate.of(1990, 5, 15), Gender.MALE, "123 Nguyen Van Linh, Da Nang");
        Patient p2 = ensurePatient("BN-20260701-0002", "Tran Thi Binh", "0914333444", LocalDate.of(1985, 8, 20), Gender.FEMALE, "456 Le Duan, Da Nang");
        ensurePatient("BN-20260701-0003", "Le Hoang Cuong", "0988555666", LocalDate.of(1995, 12, 10), Gender.MALE, "789 Dien Bien Phu, Da Nang");

        if (demoDoctor != null && p1 != null) {
            ensureAppointment("APT-20260720-001", p1, demoDoctor, room101, LocalDateTime.now().withHour(9).withMinute(0), LocalDateTime.now().withHour(9).withMinute(30), "Annual general health check-up");
        }
        if (demoDoctor != null && p2 != null) {
            ensureAppointment("APT-20260720-002", p2, demoDoctor, room101, LocalDateTime.now().withHour(10).withMinute(0), LocalDateTime.now().withHour(10).withMinute(30), "Cardiology follow-up check");
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
                        return roomRepository.findByRoomCodeIgnoreCase(code).orElse(null);
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
                        return specialtyRepository.findByNameIgnoreCase(name).orElse(null);
                    }
                    return specialtyRepository.save(Specialty.builder()
                            .name(name)
                            .description(description)
                            .active(true)
                            .build());
                });
    }

    private Staff ensureStaff(User user, String employeeCode, StaffType type) {
        java.util.Optional<Staff> existingByUser = staffRepository.findByUserId(user.getId());
        if (existingByUser.isPresent()) {
            return existingByUser.get();
        }

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

    private Doctor ensureDoctor(Staff staff, Specialty specialty, Room defaultRoom, String licenseNo, BigDecimal fee) {
        java.util.Optional<Doctor> existingByStaff = doctorRepository.findByStaffUserUserName(staff.getUser().getUserName());
        if (existingByStaff.isPresent()) {
            return existingByStaff.get();
        }
        if (doctorRepository.existsByLicenseNoIgnoreCase(licenseNo)) {
            return doctorRepository.findAll().stream().filter(d -> licenseNo.equalsIgnoreCase(d.getLicenseNo())).findFirst().orElse(null);
        }
        return doctorRepository.save(Doctor.builder()
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

    private Patient ensurePatient(String code, String name, String phone, LocalDate dob, Gender gender, String address) {
        if (patientRepository.existsByPatientCode(code)) {
            return patientRepository.search(code, org.springframework.data.domain.PageRequest.of(0, 1))
                    .stream().findFirst().orElse(null);
        }
        Patient p = new Patient();
        p.setPatientCode(code);
        p.setFullName(name);
        p.setPhone(phone);
        p.setDateOfBirth(dob);
        p.setGender(gender);
        p.setAddress(address);
        p.setStatus(PatientStatus.ACTIVE);
        return patientRepository.save(p);
    }

    private void ensureAppointment(String code, Patient patient, Doctor doctor, Room room, LocalDateTime start, LocalDateTime end, String reason) {
        if (appointmentRepository.existsByAppointmentCode(code)) {
            return;
        }
        Appointment apt = Appointment.builder()
                .appointmentCode(code)
                .patient(patient)
                .doctor(doctor)
                .room(room)
                .scheduledStart(start)
                .scheduledEnd(end)
                .reason(reason)
                .source(AppointmentSource.WALK_IN)
                .status(AppointmentStatus.BOOKED)
                .build();
        appointmentRepository.save(apt);
    }
}


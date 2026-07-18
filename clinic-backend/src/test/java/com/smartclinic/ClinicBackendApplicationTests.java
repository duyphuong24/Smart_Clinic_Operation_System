package com.smartclinic;

import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.masterdata.repository.RoomRepository;
import com.smartclinic.masterdata.repository.SpecialtyRepository;
import com.smartclinic.patient.repository.PatientRepository;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.schedule.repository.DoctorAvailabilityRepository;
import com.smartclinic.staff.repository.StaffRepository;
import com.smartclinic.user.repository.RoleRepository;
import com.smartclinic.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
        "smartclinic.seed-demo-data=false"
})
class ClinicBackendApplicationTests {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private StaffRepository staffRepository;

    @MockBean
    private DoctorRepository doctorRepository;

    @MockBean
    private SpecialtyRepository specialtyRepository;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @MockBean
    private AppointmentRepository appointmentRepository;

    @MockBean
    private QueueItemRepository queueItemRepository;

    @MockBean
    private com.smartclinic.visit.repository.VisitRepository visitRepository;

    @MockBean
    private com.smartclinic.encounter.repository.EncounterRepository encounterRepository;

    @MockBean
    private com.smartclinic.encounter.repository.EncounterServiceRepository encounterServiceRepository;

    @MockBean
    private com.smartclinic.servicecatalog.repository.ServiceCatalogRepository serviceCatalogRepository;

    @Test
    void contextLoads() {
    }
}
package com.smartclinic;

import com.smartclinic.appointment.repository.AppointmentRepository;
import com.smartclinic.doctor.repository.DoctorRepository;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.encounter.repository.EncounterServiceRepository;
import com.smartclinic.encounter.serviceorder.repository.EncounterServiceOrderRepository;
import com.smartclinic.invoice.repository.InvoiceItemRepository;
import com.smartclinic.invoice.repository.InvoiceRepository;
import com.smartclinic.masterdata.repository.RoomRepository;
import com.smartclinic.masterdata.repository.SpecialtyRepository;
import com.smartclinic.patient.repository.PatientRepository;
import com.smartclinic.payment.repository.PaymentRepository;
import com.smartclinic.queue.repository.QueueItemRepository;
import com.smartclinic.schedule.repository.DoctorAvailabilityRepository;
import com.smartclinic.servicecatalog.repository.ServiceCatalogRepository;
import com.smartclinic.staff.repository.StaffRepository;
import com.smartclinic.user.repository.RoleRepository;
import com.smartclinic.user.repository.UserRepository;
import com.smartclinic.visit.repository.VisitRepository;
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
    private VisitRepository visitRepository;

    @MockBean
    private EncounterRepository encounterRepository;

    @MockBean
    private EncounterServiceRepository encounterServiceRepository;

    @MockBean
    private ServiceCatalogRepository serviceCatalogRepository;

    @MockBean
    private EncounterServiceOrderRepository encounterServiceOrderRepository;

    @MockBean
    private InvoiceRepository invoiceRepository;

    @MockBean
    private InvoiceItemRepository invoiceItemRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @Test
    void contextLoads() {
    }
}
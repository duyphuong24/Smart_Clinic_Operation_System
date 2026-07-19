# Smart Clinic Operations System - Team Task Allocation

## 1. Project Goal

Smart Clinic Operations System is a Spring Boot, Thymeleaf, and JavaFX project for clinic operations. The project focuses on one complete workflow instead of isolated CRUD screens.

Core workflow:

```text
Login / RBAC
-> Patient registration
-> Doctor schedule
-> Appointment booking
-> Patient check-in
-> Queue management
-> Doctor consultation
-> Invoice generation
-> Payment recording
-> Basic dashboard / demo report
```

Current project structure:

```text
smart-clinic-operations-system
|-- clinic-backend
|-- clinic-desktop
|-- docs
|-- task_allocation.md
`-- team_rules.md
```

Important MVP decision: `AuditLog` is not part of the one-week critical path. It is optional after the core workflow is stable.

## 1.1 Current Truth On Dev

As of the latest integration on `dev` (up to issue #47):
- **Completed Core Workflows (Backend):** Patient CRUD, Doctor/Specialty/Room master data, Doctor Schedule availability validation, Appointment booking/rescheduling/cancellation, Patient check-in, Queue Item generation and doctor call/skip flow, Visit & Encounter creation, Service catalog & order billing, Invoice generation, and Payment processing.
- **Reporting/Audit Log:** Audit Log is partially implemented (foundation is merged but not fully wired to all modules).
- **JavaFX Desktop App:** Authentication, login, and async `ApiClient` HTTP foundation are implemented.
- **Role-based API Authorization and JWT Refresh Token:** Currently being upgraded (Enforcing JWT Base64 signer verification, database refresh tokens storage, and strict role endpoints protection).

## 2. Architecture Rule

The project has two presentation layers:

```text
Thymeleaf MVC Controller
        |
        v
Service Layer + Business Rules
        |
        v
Repository Layer
        |
        v
SQL Server
        ^
        |
REST API Controller
        ^
        |
JavaFX Desktop Client
```

Rules:

- Controllers do not contain business logic.
- REST controllers and Thymeleaf controllers call the same service layer.
- JavaFX controllers only handle UI events.
- JavaFX services call API clients.
- API clients call Spring Boot REST endpoints.
- Business validation belongs in backend services.

## 3. Team Members

| Member | Name | Main Role | Main Responsibility |
| --- | --- | --- | --- |
| Member 1 - Leader | Nguyễn Duy Phương | Backend Core + Architecture + JavaFX REST Integration | Security, database, API contract, appointment, queue, integration review |
| Member 2 | Trịnh Hoàng Thiên Bảo | Thymeleaf Web Portal | Web layout, patient/doctor/schedule/appointment/encounter/dashboard UI |
| Member 3 | Nguyễn Hữu Tài | JavaFX UI + Billing/Payment | JavaFX screens, cashier workflow, invoice/payment backend/UI |

Boundary decision:

- Member 1 owns REST integration in JavaFX.
- Member 3 owns JavaFX UI/FXML/controllers and payment workflow screens.
- Member 3 should call JavaFX service interfaces, not raw HTTP clients directly.

## 4. Tech Stack

- Java 21 currently configured; requirement allows Java 17+.
- Spring Boot 3.3.x.
- Maven.
- Spring MVC.
- Spring Data JPA + Hibernate 6.x.
- Thymeleaf + Thymeleaf Security Extras.
- Spring Security + BCrypt.
- JavaFX 21 currently configured; requirement allows JavaFX 17+.
- SQL Server 2019/2022.
- Postman for REST API testing.
- JUnit 5 and Mockito where useful.

The project uses SQL Server local and `application.properties`. Do not convert config to `application.yml`.

## 5. Current Package Structure

Backend uses domain-based packages under:

```text
clinic-backend/src/main/java/com/smartclinic
```

Main packages:

```text
auth
security
user
staff
doctor
specialty
room
patient
schedule
appointment
queue
visit
encounter
servicecatalog
billing
payment
report
common
config
```

Package responsibility rules:

| Package | Responsibility |
| --- | --- |
| `auth` | Login, logout, authentication request/response, authentication use cases. It should not own user profile CRUD. |
| `security` | Spring Security infrastructure such as `SecurityConfig`, `UserDetailsService`, principals, password encoder, and security helpers. It should not contain clinic business logic. |
| `user` | Account and role management: `User`, `Role`, user-role mapping, account status, admin user CRUD. |
| `staff` | Employee profile shared by receptionist, cashier, manager, and doctor. |
| `doctor` | Doctor-specific profile such as license number, specialty, default room, consultation fee. |
| `specialty` | Specialty CRUD. |
| `room` | Room CRUD. |
Main business modules use this internal structure:

```text
controller
rest
service
repository
entity
dto
mapper
validation
```

Example:

```text
com.smartclinic.appointment.controller
com.smartclinic.appointment.rest
com.smartclinic.appointment.service
com.smartclinic.appointment.repository
com.smartclinic.appointment.entity
com.smartclinic.appointment.dto
com.smartclinic.appointment.mapper
com.smartclinic.appointment.validation
```

Desktop uses packages under:

```text
clinic-desktop/src/main/java/com/smartclinic/desktop
```

Main packages:

```text
app
controller
service
api
dto
session
navigation
config
util
```

## 6. Module Ownership Summary

| Module | Member 1 - Leader | Member 2 - Web | Member 3 - Desktop/Billing |
| --- | --- | --- | --- |
| Project foundation | Main | Support | Support |
| Database design | Main | Review | Review |
| Status flow | Main | Follow | Follow |
| Security/RBAC | Main | Role-based menu | Login UI support |
| Patient | Backend/API | Web UI | Patient search UI |
| Doctor/Specialty/Room | Backend | Web UI | No |
| Doctor schedule | Backend validation | Web UI | No |
| Appointment | Backend rules/API | Web UI | JavaFX screen |
| Check-in | Backend core/API | Web action | JavaFX check-in UI |
| Queue | Backend core/API | Web queue board | JavaFX queue board |
| Visit/Encounter | Backend support | Main consultation UI | No |
| Service catalog | Backend | Web support | Use in billing |
| Billing | Review/integration | Web support | Main backend/UI |
| Payment | Review/integration | Web support | Main backend/UI |
| JavaFX REST integration | Main | No | UI/service interface support |
| Basic report/dashboard | Query support | Main UI | No |
| Testing | Core/API tests | Web manual tests | JavaFX/manual tests |
| README/demo | Main | Screenshots | Screenshots |

## 7. Member 1 - Nguyễn Duy Phương: Leader Tasks

### 7.1 Ownership

Leader owns these project-wide decisions:

- Architecture.
- Database schema.
- Entity relationships.
- Enum/status flow.
- Security/RBAC.
- Service business rules.
- REST API contract.
- JavaFX REST integration.
- Integration review.
- Demo flow.

### 7.2 First Tasks To Unblock Team

These tasks should be completed or drafted before members implement deeply:

- [X] Create Spring Boot Maven project.
- [X] Configure SQL Server in `application.properties`.
- [X] Create domain-based package structure under `com.smartclinic`.
- [X] Define naming rules in `team_rules.md`.
- [X] Draft team task allocation.
- [X] Draft database design in `docs/database-design.md`.
- [X] Draft status flow in `docs/status-flow.md`.
- [X] Create `BaseEntity` with `id`, `createdAt`, and `updatedAt`.
- [X] Create common API response format.
- [X] Create global exception handling for REST and web.
- [X] Create Spring Security skeleton.
- [X] Seed demo accounts by role.
- [X] Draft API contract in `docs/api-contract.md`.
- [X] Create sample health endpoint for JavaFX connectivity test.

Recommended `BaseEntity` decision:

```text
BaseEntity: id, createdAt, updatedAt
```

Do not put `active` or `version` in `BaseEntity`; status/active rules differ per entity and optimistic locking is out of MVP scope.

### 7.3 Backend Foundation Status

Current foundation status:

- [X] Maven `pom.xml` configured.
- [X] SQL Server driver configured.
- [X] Spring Data JPA dependency configured.
- [X] Thymeleaf dependency configured.
- [X] Spring Security dependency configured.
- [X] Validation dependency configured.
- [X] Static resource folders created.
- [X] Domain-based package structure created.
- [X] Backend test compiles with current skeleton.

Current backend package style is domain-based, not global layered. Do not create a second global structure such as `controller/service/repository/entity` at the root. CRUD modules such as `user`, `staff`, `specialty`, and `room` should also follow the standard business module subpackages. Technical modules such as `auth` and `security` only create subpackages that match their real responsibilities.

### 7.4 Security/RBAC

Roles:

```text
ROLE_ADMIN
ROLE_RECEPTIONIST
ROLE_DOCTOR
ROLE_CASHIER
ROLE_MANAGER
```

Tasks:

- [X] Implement `User`, `Role`, `Staff`, and `Doctor` relationship.
- [X] Configure login/logout with Spring Security.
- [X] Configure BCrypt password encoder.
- [ ] Configure role-based web URL access.
- [ ] Configure role-based REST API access.
- [X] Add access denied page.
- [X] Seed demo users for all roles.

Access summary:

| Role | Permission |
| --- | --- |
| `ADMIN` | Full system access |
| `RECEPTIONIST` | Patient, appointment, check-in, queue |
| `DOCTOR` | Queue, consultation, encounter |
| `CASHIER` | Invoice and payment |
| `MANAGER` | Dashboard and reports |

### 7.5 Appointment and Queue Backend

Appointment tasks:

- [X] Create appointment.
- [X] View appointments by date.
- [X] View today appointments.
- [X] Reschedule appointment.
- [X] Cancel appointment.
- [X] Validate doctor availability.
- [X] Prevent duplicate doctor slot in service layer.
- [X] Prevent invalid status transition.

Queue tasks:

- [X] Check in appointment.
- [X] Create queue item after check-in.
- [X] Create walk-in queue item.
- [X] Generate queue number.
- [X] View today's queue.
- [X] Filter queue by doctor.
- [X] Doctor calls next patient.
- [X] Update queue status.

Rules:

- Only `BOOKED` appointment can be checked in.
- One appointment can create only one queue item.
- Queue number is unique per day.
- Completed/skipped queue items do not appear in active queue.

### 7.6 REST API Contract and JavaFX Integration

Base prefix:

```text
/api/v1
```

Minimum API draft for MVP:

```text
GET  /api/v1/health
POST /api/v1/auth/login
POST /api/v1/auth/logout

GET  /api/v1/patients/search?keyword=
GET  /api/v1/patients/{id}
POST /api/v1/patients

GET   /api/v1/appointments/today
GET   /api/v1/appointments?date=
POST  /api/v1/appointments
PUT   /api/v1/appointments/{id}/reschedule
PATCH /api/v1/appointments/{id}/cancel
PATCH /api/v1/appointments/{id}/check-in

GET   /api/v1/queue-items/today
GET   /api/v1/queue-items/today?doctorId=
PATCH /api/v1/queue-items/{id}/call
PATCH /api/v1/queue-items/{id}/skip
PATCH /api/v1/queue-items/{id}/complete

GET  /api/v1/invoices/pending
GET  /api/v1/invoices/{id}
POST /api/v1/invoices/generate?visitId=
POST /api/v1/invoices/{id}/payments
```

JavaFX REST integration tasks for Member 1:

- [ ] Define DTOs used by JavaFX.
- [X] Define API response format.
- [X] Implement auth session/token/cookie handling strategy.
- [X] Implement JavaFX API client wrapper.
- [ ] Implement JavaFX service implementations that call API clients.
- [ ] Connect Member 3 screens to real APIs.
- [ ] Map API errors to JavaFX alerts.

## 8. Member 2 - Trịnh Hoàng Thiên Bảo: Web Thymeleaf Tasks

Member 2 owns the web portal used by Admin, Receptionist, Doctor, and Manager.

### 8.1 Layout and Common UI

- [ ] Create base Thymeleaf layout.
- [ ] Create header/sidebar.
- [ ] Create role-based menu.
- [ ] Create breadcrumb.
- [ ] Create flash message component.
- [ ] Create form error display.
- [ ] Create common table style.
- [ ] Create 403 and 404 pages.

Rules:

- No inline CSS.
- Common CSS goes to `static/css/custom.css`.
- Module CSS goes to `static/css/<module>.css`.
- Use Thymeleaf fragments.

### 8.2 Patient Web UI

- [ ] Patient list page.
- [ ] Patient search/filter.
- [ ] Patient create form.
- [ ] Patient update form.
- [ ] Patient detail page.
- [ ] Patient visit history section.

### 8.3 Doctor, Specialty, Room, Schedule Web UI

- [ ] Specialty list/create/update pages.
- [ ] Room list/create/update pages.
- [ ] Doctor list/create/update/detail pages.
- [ ] Doctor availability list/create/update pages.
- [ ] Activate/deactivate availability action.

Schedule validation is handled by Member 1 service layer.

### 8.4 Appointment and Queue Web UI

- [ ] Today appointments page.
- [ ] Appointment list by date.
- [ ] Create appointment form.
- [ ] Appointment detail page.
- [ ] Reschedule appointment form.
- [ ] Cancel appointment action.
- [ ] Check-in action.
- [ ] Queue board page.
- [ ] Queue filter by doctor/room.

Check-in UI calls backend service/API only. Do not implement check-in rules in templates.

### 8.5 Encounter and Dashboard UI

- [ ] Doctor queue page.
- [ ] Start consultation action.
- [ ] Encounter form.
- [ ] Chief complaint, diagnosis, clinical note inputs.
- [ ] Add service order section.
- [ ] Complete encounter action.
- [ ] Completed encounter detail page.
- [ ] Manager dashboard page.
- [ ] Today appointments, waiting queue, completed visits, today revenue cards.
- [ ] Top services and doctor performance tables.

### 8.6 Member 2 Deliverables

- [ ] Web layout looks professional.
- [ ] Patient pages work.
- [ ] Doctor/specialty/room pages work.
- [ ] Doctor schedule pages work.
- [ ] Appointment pages work.
- [ ] Queue board works.
- [ ] Doctor consultation pages work.
- [ ] Dashboard displays MVP metrics.
- [ ] Web UI is ready for demo.

## 9. Member 3 - Nguyễn Hữu Tài: JavaFX UI + Billing/Payment Tasks

Member 3 owns JavaFX desktop UI and billing/payment workflow. Member 1 owns the actual REST integration.

### 9.1 JavaFX Structure

Use current package structure:

```text
com.smartclinic.desktop.app
com.smartclinic.desktop.controller
com.smartclinic.desktop.service
com.smartclinic.desktop.api
com.smartclinic.desktop.dto
com.smartclinic.desktop.session
com.smartclinic.desktop.navigation
com.smartclinic.desktop.config
com.smartclinic.desktop.util
```

Required flow:

```text
FXML Controller -> Desktop Service -> API Client -> Backend REST API
```

### 9.2 JavaFX Screens

- [ ] Login screen.
- [ ] Main layout/navigation.
- [ ] Today appointments screen.
- [ ] Patient search screen.
- [ ] Quick walk-in screen.
- [ ] Check-in confirmation dialog.
- [ ] Queue board screen.
- [ ] Pending invoices screen.
- [ ] Invoice detail screen.
- [ ] Payment screen.

### 9.3 Billing and Payment

Invoice MVP status flow:

```text
ISSUED -> PAID
ISSUED -> CANCELLED
```

Payment MVP statuses:

```text
SUCCESS
FAILED
```

Tasks:

- [ ] Generate invoice from completed visit.
- [ ] Add consultation fee and encounter services.
- [ ] Calculate subtotal and total.
- [ ] Prevent duplicate invoice for same visit.
- [ ] Prevent editing paid invoice.
- [ ] View pending invoices.
- [ ] View invoice detail.
- [ ] Record payment.
- [ ] Mark invoice as `PAID` on successful payment.
- [ ] Display payment result in JavaFX.

Business rules:

- Only `ISSUED` invoice can be paid.
- Payment amount must equal invoice total in MVP.
- Paid invoice cannot be modified.
- One invoice has one successful full payment in MVP.

### 9.4 Member 3 Deliverables

- [ ] JavaFX app opens successfully.
- [ ] Login screen is complete.
- [ ] Today appointments screen is complete.
- [ ] Patient search screen is complete.
- [ ] Check-in UI flow is complete.
- [ ] Queue board desktop screen is complete.
- [ ] Pending invoice screen works.
- [ ] Payment screen works.
- [ ] JavaFX app is ready for demo after REST connection.

## 10. Core Entities and Relationships

MVP entities:

```text
User
Role
Staff
Doctor
Specialty
Room
Patient
DoctorAvailability
Appointment
QueueItem
Visit
Encounter
ServiceCatalog
EncounterService
Invoice
InvoiceItem
Payment
```

Excluded from MVP critical path:

```text
AuditLog
Prescription
MedicineInventory
Insurance
Notification
Refund
PDF invoice
Advanced reports
```

Relationship baseline is documented in `docs/database-design.md`.

Status flows are documented in `docs/status-flow.md`.

## 11. Milestones

### Milestone 1 - Foundation

Owner: Member 1, support by all.

- [X] Backend project setup.
- [X] SQL Server config.
- [X] Package structure.
- [X] JavaFX skeleton.
- [X] Team rules.
- [X] Database design draft.
- [X] Status flow draft.
- [X] Security skeleton.
- [X] Seed data.
- [X] API contract draft.
- [ ] Base web layout.

### Milestone 2 - Clinic Master Data

Owner: Member 1 + Member 2.

- [ ] Patient.
- [ ] Doctor.
- [ ] Specialty.
- [ ] Room.
- [ ] Doctor availability.

### Milestone 3 - Appointment and Queue

Owner: all members.

- [ ] Appointment booking.
- [ ] Appointment validation.
- [ ] Check-in.
- [ ] Walk-in queue item.
- [ ] Queue board.
- [ ] JavaFX appointment screen.
- [ ] JavaFX queue screen.

### Milestone 4 - Consultation and Billing

Owner: Member 2 + Member 3, reviewed by Member 1.

- [ ] Encounter.
- [ ] Service orders.
- [ ] Invoice.
- [ ] Payment.

### Milestone 5 - Reports, Testing, Demo

Owner: all members.

- [ ] Basic dashboard.
- [ ] Business rule tests.
- [ ] Postman collection.
- [ ] Desktop test plan.
- [ ] README.
- [ ] Screenshots.
- [ ] Demo script.

## 12. One-Week Execution Plan

### Day 1 - Foundation

Member 1:

- [ ] BaseEntity.
- [ ] Common API response.
- [ ] Global exception handling.
- [ ] Security skeleton.
- [ ] User/Role seed data.
- [ ] API contract draft.

Member 2:

- [ ] Thymeleaf base layout.
- [ ] Sidebar/header.
- [ ] Patient UI mock pages.

Member 3:

- [ ] JavaFX navigation.
- [ ] Login screen mock.
- [ ] Service interface skeleton for screens.

### Day 2 - Master Data

Member 1:

- [ ] Patient backend.
- [ ] Doctor backend.
- [ ] Schedule backend.
- [ ] Schedule validation service.

Member 2:

- [ ] Patient web pages.
- [ ] Doctor web pages.
- [ ] Specialty/Room pages.
- [ ] Schedule pages.

Member 3:

- [ ] Patient search UI.
- [ ] Today appointment UI mock.

### Day 3 - Appointment

Member 1:

- [ ] Appointment create logic.
- [ ] Appointment reschedule/cancel.
- [ ] Duplicate doctor slot validation.
- [ ] Doctor availability validation.
- [ ] Appointment REST endpoints.

Member 2:

- [ ] Appointment list page.
- [ ] Create appointment form.
- [ ] Reschedule/cancel UI.
- [ ] Today appointments page.

Member 3:

- [ ] Today appointments JavaFX screen.
- [ ] TableView and filters.
- [ ] Check-in button UI.

### Day 4 - Check-in and Queue

Member 1:

- [ ] Check-in backend.
- [ ] Queue item generation.
- [ ] Queue status flow.
- [ ] Queue REST API.
- [ ] JavaFX appointment/queue REST integration.

Member 2:

- [ ] Queue web board.
- [ ] Check-in action on web.
- [ ] Queue filter by doctor/room.

Member 3:

- [ ] JavaFX check-in UI.
- [ ] JavaFX queue board.
- [ ] Loading/error states.

### Day 5 - Consultation and Billing

Member 1:

- [ ] Encounter integration support.
- [ ] Review billing schema.
- [ ] Review payment business rules.
- [ ] JavaFX invoice/payment REST integration support.

Member 2:

- [ ] Doctor queue page.
- [ ] Consultation form.
- [ ] Encounter service order UI.
- [ ] Complete encounter UI.

Member 3:

- [ ] Invoice generation backend/UI.
- [ ] Payment backend/UI.
- [ ] Pending invoices screen.

### Day 6 - Reports and Polish

Member 1:

- [ ] Core business rule tests.
- [ ] Security tests.
- [ ] Integration review.
- [ ] Postman collection core cases.

Member 2:

- [ ] Dashboard UI.
- [ ] UI polish.
- [ ] Form validation display.

Member 3:

- [ ] JavaFX UI polish.
- [ ] Payment flow polish.
- [ ] Error dialog polish.
- [ ] Desktop test cases.

### Day 7 - Final Integration and Demo

All:

- [ ] Full workflow manual test.
- [ ] Fix integration bugs.
- [ ] Prepare demo data.
- [ ] Prepare README.
- [ ] Add screenshots.
- [ ] Prepare demo script.
- [ ] Final code review.

## 13. MVP Must-Have Features

- [ ] Login/RBAC.
- [ ] Patient management.
- [ ] Doctor management.
- [ ] Doctor schedule.
- [ ] Appointment booking.
- [ ] Appointment validation.
- [ ] Patient check-in.
- [ ] Queue management.
- [ ] Doctor consultation.
- [ ] Invoice generation.
- [ ] Payment recording.
- [ ] JavaFX check-in/queue.
- [ ] Basic dashboard.
- [ ] Postman collection with at least 20 requests.
- [ ] Desktop test plan with at least 10 test cases.

## 14. Optional Features

Only implement these if the MVP is already stable:

- [Partial] Audit log (foundation is merged but not fully wired to all modules).
- [ ] Prescription.
- [ ] Medicine inventory.
- [ ] Notification.
- [ ] PDF invoice.
- [ ] Excel/PDF report export.
- [ ] Advanced chart.
- [ ] WebSocket real-time queue.
- [ ] Multi-branch clinic.
- [ ] Insurance claim.
- [ ] Refund workflow.

## 15. Definition of Done

A task is done only when:

- [ ] Entity/repository is implemented if needed.
- [ ] Service interface and implementation are completed.
- [ ] Business rules are validated in service.
- [ ] Controller/API is implemented.
- [ ] DTO/request/response is used correctly.
- [ ] Error handling works.
- [ ] Role permission is checked when applicable.
- [ ] UI is connected if required.
- [ ] Manual test is completed.
- [ ] Demo data works.
- [ ] No inline CSS in Thymeleaf.
- [ ] JavaFX controller does not contain business logic.

## 16. Final Demo Flow

1. Admin logs in.
2. Admin creates specialty, room, doctor, and schedule.
3. Receptionist logs in.
4. Receptionist creates/searches patient.
5. Receptionist books appointment.
6. Receptionist checks in patient.
7. Patient appears in queue.
8. Doctor logs in.
9. Doctor calls next patient.
10. Doctor starts consultation.
11. Doctor records diagnosis and service orders.
12. Doctor completes encounter.
13. Cashier opens JavaFX cashier/front desk screen.
14. Cashier generates invoice.
15. Cashier records payment.
16. Manager views dashboard.

## 17. Git Workflow

Branches:

- `main`: stable branch.
- `dev`: team integration branch.
- `feature/<issue-id>-<short-name>`: feature work.
- `bugfix/<issue-id>-<short-name>`: bug fix.

Commit format:

```text
feat(auth): add role based login
fix(queue): prevent duplicate check-in
test(appointment): add duplicate slot tests
docs(readme): update setup guide
style(css): polish patient table
chore(pom): add sql server dependency
```

Rules:

- Pull latest `dev` before starting a feature branch.
- Feature branches should target `dev`.
- `main` should receive stable work only after integration testing.
- Do not commit `.env`, `.idea`, `target`, or local secret files.
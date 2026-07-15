# Smart Clinic Operations System - Task Allocation

## 1. Mục tiêu dự án

Smart Clinic Operations System là hệ thống quản lý vận hành phòng khám, gồm:

- Web app: Spring Boot + Thymeleaf + Spring Security + SQL Server.
- Desktop app: JavaFX kết nối backend qua REST API.
- Cùng dùng một database và cùng tuân thủ business workflow của phòng khám.

Luồng nghiệp vụ chính:

```text
Patient Registration
-> Doctor Schedule
-> Appointment Booking
-> Patient Check-in
-> Queue Management
-> Doctor Consultation
-> Invoice Generation
-> Payment
-> Dashboard / Report
-> Audit Log
```

Nguyên tắc chia việc: chia theo workflow và ownership, không chia kiểu mỗi người vài CRUD rời rạc.

## 2. Kiến trúc thống nhất

```text
Thymeleaf Web Controller
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

Quy tắc bắt buộc:

- Web controller và REST controller chỉ nhận request, validate input cơ bản, gọi service và trả response.
- Toàn bộ business logic nằm trong service layer.
- JavaFX controller chỉ xử lý event UI, không gọi HTTP client trực tiếp.
- JavaFX service/client gọi REST API do backend cung cấp.
- Không tạo entity, enum, status flow tùy ý nếu chưa được leader chốt.

## 3. Thành viên và vai trò

| Member | Họ tên | Vai trò chính | Trọng tâm |
| --- | --- | --- | --- |
| Member 1 - Leader | Nguyễn Duy Phương | Backend Core + Architecture + REST Integration | Security, database, appointment, queue, API contract, JavaFX REST integration |
| Member 2 | Trịnh Hoàng Thiên Bảo | Thymeleaf Web Portal | Patient UI, doctor/admin UI, appointment UI, encounter UI, dashboard |
| Member 3 | Nguyễn Hữu Tài | JavaFX UI + Billing/Payment | JavaFX screens, cashier workflow, invoice/payment backend/UI |

Ghi chú quan trọng: phần REST integration trong JavaFX do Member 1 - Nguyễn Duy Phương phụ trách. Member 3 làm JavaFX UI, controller, FXML, flow màn hình và phối hợp với Member 1 để nối API.

## 4. Tech stack cần bám theo yêu cầu môn học

- Java 17+
- Spring Boot 3.x
- Maven
- Spring MVC
- Spring Data JPA + Hibernate 6.x
- Thymeleaf 3.x + Thymeleaf Security Extras
- Spring Security + BCrypt
- JavaFX 17+ + FXML + Scene Builder
- SQL Server 2019/2022
- Postman Collection + Environment
- JUnit 5, Mockito, REST Assured hoặc MockMvc
- Git/GitHub: issue, branch, pull request, conventional commit

Không nên dùng PostgreSQL cho bản nộp chính nếu requirement môn yêu cầu SQL Server.

## 5. Module ownership tổng quan

| Module | Member 1 - Nguyễn Duy Phương | Member 2 - Trịnh Hoàng Thiên Bảo | Member 3 - Nguyễn Hữu Tài |
| --- | --- | --- | --- |
| Project setup | Main | Support | Support |
| Database schema | Main | Review | Review |
| Security/RBAC | Main | Role-based menu | Desktop login UI support |
| Patient | Backend/API | Web UI | JavaFX search UI |
| Doctor/Specialty/Room | Backend | Web UI | No |
| Doctor Schedule | Backend validation | Web UI | No |
| Appointment | Backend rules/API | Web UI | JavaFX screen |
| Check-in | Backend core/API | Web button/page | JavaFX screen |
| Queue | Backend core/API | Web queue board | JavaFX queue board |
| Encounter | Backend support | Main Web UI | No |
| Service Catalog | Backend | Web UI | Use in invoice flow |
| Billing | Architecture review | Web support | Main backend/UI |
| Payment | Architecture review | Web support | Main backend/UI |
| JavaFX REST Integration | Main | No | UI support |
| Report | Query support | Main UI | No |
| Audit Log | Backend core | Viewer UI | Trigger actions |
| Testing | Core tests/API tests | Web manual tests | JavaFX/manual tests |
| README/Demo | Main | Screenshots | Screenshots |

## 6. Member 1 - Nguyễn Duy Phương: Leader tasks

### 6.1 Ownership chính

Leader giữ các phần quyết định chất lượng và khả năng tích hợp:

- Architecture.
- Database schema.
- Entity relationship.
- Enum/status flow.
- Security/RBAC.
- Service business rules.
- REST API contract.
- JavaFX REST integration.
- Integration review.
- Demo flow.

### 6.2 Việc cần làm đầu tiên để unblock team

Các task này nên làm trước để Bảo và Tài có thể bắt đầu mà không bị chờ:

- [ ] Tạo project Spring Boot Maven chuẩn.
- [ ] Cấu hình SQL Server trong `application.properties`.
- [ ] Tạo package structure chuẩn theo requirement.
- [ ] Chốt naming convention: entity, DTO, request/response, service interface.
- [ ] Chốt core entities và relationships.
- [ ] Chốt enum status flow cho appointment, queue, encounter, invoice, payment.
- [ ] Tạo base entity có `id`, `createdAt`, `updatedAt`, `@Version`, `isActive` nếu cần.
- [ ] Tạo global exception handling cho web và REST API.
- [ ] Tạo common API response format.
- [ ] Tạo Spring Security skeleton.
- [ ] Seed demo accounts theo role.
- [ ] Tạo API contract markdown hoặc Postman draft cho JavaFX.
- [ ] Tạo sample endpoint để Member 3 test JavaFX UI flow.

### 6.3 Backend foundation

- [ ] Configure Maven `pom.xml`.
- [ ] Configure SQL Server driver.
- [ ] Configure Spring Data JPA.
- [ ] Configure Thymeleaf.
- [ ] Configure Spring Security.
- [ ] Configure validation dependency.
- [ ] Configure static resources.
- [ ] Create package structure:

```text
src/main/java/com/hsf302/smartclinic
├── config
├── controller
├── restcontroller
├── service
│   ├── interfaces
│   └── impl
├── repository
├── entity
├── dto
├── exception
├── security
└── util
```

### 6.4 Security/RBAC

Roles nên dùng cho domain phòng khám:

- `ROLE_ADMIN`
- `ROLE_RECEPTIONIST`
- `ROLE_DOCTOR`
- `ROLE_CASHIER`
- `ROLE_MANAGER`

Task:

- [ ] Implement `User`, `Role`, `Staff`, `Doctor` relationship.
- [ ] Login/logout bằng Spring Security.
- [ ] BCrypt password.
- [ ] Role-based URL access cho web.
- [ ] Role-based URL access cho REST API.
- [ ] Method security ở service nếu cần.
- [ ] Access denied page.
- [ ] Seed demo accounts:
  - admin
  - receptionist
  - doctor
  - cashier
  - manager

Rule:

- `ADMIN`: full access.
- `RECEPTIONIST`: patient, appointment, check-in, queue.
- `DOCTOR`: queue, consultation, encounter.
- `CASHIER`: invoice, payment.
- `MANAGER`: dashboard, reports.

### 6.5 Appointment business logic

Task:

- [ ] Create appointment.
- [ ] View appointments by date.
- [ ] View today appointments.
- [ ] Reschedule appointment.
- [ ] Cancel appointment.
- [ ] Validate doctor availability.
- [ ] Prevent duplicate doctor slot.
- [ ] Prevent invalid status transition.

Status:

```text
BOOKED
-> CHECKED_IN
-> IN_CONSULTATION
-> COMPLETED

BOOKED -> CANCELLED
BOOKED -> NO_SHOW
```

Business rules:

- [ ] Cannot book outside doctor working hours.
- [ ] Cannot book duplicate slot for same doctor.
- [ ] Cannot check in cancelled appointment.
- [ ] Cannot cancel appointment after consultation starts.
- [ ] Cannot reschedule completed appointment.

### 6.6 Check-in and queue

Task:

- [ ] Check in appointment.
- [ ] Create queue item after check-in.
- [ ] Create walk-in queue item.
- [ ] Generate queue number.
- [ ] View today queue.
- [ ] Filter queue by doctor.
- [ ] Doctor calls next patient.
- [ ] Update queue status.

Status:

```text
WAITING
-> CALLED
-> IN_SERVICE
-> DONE

WAITING -> SKIPPED
```

Business rules:

- [ ] Only `BOOKED` appointment can be checked in.
- [ ] One appointment can only be checked in once.
- [ ] Queue number is unique per day.
- [ ] Doctor can only call assigned patients.
- [ ] `DONE` and `SKIPPED` queue items should not appear in active queue.

### 6.7 REST API contract and JavaFX integration

Leader giữ phần này để JavaFX không bị lệch contract.

Base prefix:

```text
/api/v1
```

Auth:

```text
POST /api/v1/auth/login
POST /api/v1/auth/logout
```

Patient:

```text
GET  /api/v1/patients/search?keyword=
GET  /api/v1/patients/{id}
POST /api/v1/patients
```

Appointment:

```text
GET  /api/v1/appointments/today
GET  /api/v1/appointments?date=
POST /api/v1/appointments
PUT  /api/v1/appointments/{id}/reschedule
PATCH /api/v1/appointments/{id}/cancel
PATCH /api/v1/appointments/{id}/check-in
```

Queue:

```text
GET   /api/v1/queue/today
GET   /api/v1/queue/today?doctorId=
PATCH /api/v1/queue/{id}/call
PATCH /api/v1/queue/{id}/skip
PATCH /api/v1/queue/{id}/complete
```

Invoice/payment:

```text
GET  /api/v1/invoices/pending
GET  /api/v1/invoices/{id}
POST /api/v1/invoices/generate?visitId=
POST /api/v1/invoices/{id}/payments
```

JavaFX integration tasks của leader:

- [ ] Define DTOs used by JavaFX.
- [ ] Define API response format.
- [ ] Implement auth session/token/cookie handling strategy.
- [ ] Implement JavaFX `ApiClient`/HTTP wrapper.
- [ ] Implement `AuthApiClient`.
- [ ] Implement `PatientApiClient`.
- [ ] Implement `AppointmentApiClient`.
- [ ] Implement `QueueApiClient`.
- [ ] Implement `InvoiceApiClient`.
- [ ] Implement `PaymentApiClient`.
- [ ] Connect Member 3's JavaFX screens to real APIs.
- [ ] Handle API errors and map them to JavaFX alerts.

### 6.8 Deliverables của Member 1

- [ ] Backend project runs successfully.
- [ ] SQL Server connection works.
- [ ] Login by role works.
- [ ] Role-based security works.
- [ ] Core schema is stable.
- [ ] Appointment booking works with validation.
- [ ] Check-in creates queue item.
- [ ] Queue status flow works.
- [ ] REST APIs are stable.
- [ ] JavaFX REST integration works.
- [ ] Demo data is available.
- [ ] Core business rules have unit tests.
- [ ] Postman collection has core happy path and error cases.

## 7. Member 2 - Trịnh Hoàng Thiên Bảo: Web Thymeleaf tasks

### 7.1 Ownership chính

Bảo phụ trách web portal cho Admin, Receptionist, Doctor và Manager.

Modules:

- Thymeleaf layout.
- Patient web UI.
- Doctor/Specialty/Room web UI.
- Doctor schedule web UI.
- Appointment web UI.
- Encounter/consultation web UI.
- Dashboard/report UI.
- Audit log viewer.

### 7.2 Thymeleaf layout

- [ ] Create base layout.
- [ ] Create header.
- [ ] Create sidebar.
- [ ] Create role-based menu.
- [ ] Create breadcrumb.
- [ ] Create flash message component.
- [ ] Create form error display.
- [ ] Create common table style.
- [ ] Create 403 page.
- [ ] Create 404 page.

Rules:

- No inline CSS.
- Common CSS goes to `static/css/custom.css`.
- Module CSS goes to `static/css/<module>.css`.
- Use Bootstrap 5.
- Use Thymeleaf fragments.

### 7.3 Patient web UI

- [ ] Patient list page.
- [ ] Patient search/filter.
- [ ] Patient create form.
- [ ] Patient update form.
- [ ] Patient detail page.
- [ ] Patient visit history section.

Fields:

- Patient code.
- Full name.
- Date of birth.
- Gender.
- Phone.
- Email.
- Address.
- Emergency contact name.
- Emergency contact phone.
- Allergy note.
- Status.

### 7.4 Doctor, specialty, room web UI

- [ ] Specialty list page.
- [ ] Specialty create/update form.
- [ ] Doctor list page.
- [ ] Doctor create/update form.
- [ ] Doctor detail page.
- [ ] Room list page.
- [ ] Room create/update form.

Doctor fields:

- Full name.
- Specialty.
- License number.
- Consultation fee.
- Room.
- Status.
- Bio.

### 7.5 Doctor schedule web UI

- [ ] Doctor availability list.
- [ ] Create doctor availability form.
- [ ] Update doctor availability form.
- [ ] Activate/deactivate availability.

Fields:

- Doctor.
- Day of week.
- Start time.
- End time.
- Slot duration.
- Room.
- Active status.

Note: Bảo làm UI và controller gọi service. Validate lịch đúng/sai nằm trong service của Member 1.

### 7.6 Appointment web UI

- [ ] Today appointments page.
- [ ] Appointment list by date.
- [ ] Create appointment form.
- [ ] Appointment detail page.
- [ ] Reschedule appointment form.
- [ ] Cancel appointment action.
- [ ] Check-in button.

Note: Check-in button chỉ gọi service/API đã có, không viết logic check-in trong template/controller.

### 7.7 Encounter/consultation web UI

- [ ] Doctor queue page.
- [ ] Start consultation action.
- [ ] Encounter form.
- [ ] Chief complaint input.
- [ ] Diagnosis input.
- [ ] Clinical note input.
- [ ] Add service order section.
- [ ] Complete encounter action.
- [ ] Completed encounter detail page.

Rules:

- [ ] Only doctor can start consultation.
- [ ] Only assigned doctor can complete encounter.
- [ ] Completed encounter is read-only.
- [ ] Encounter must be completed before invoice generation.

### 7.8 Dashboard/report UI

- [ ] Manager dashboard page.
- [ ] Today appointments card.
- [ ] Waiting queue count card.
- [ ] Completed visits card.
- [ ] Today revenue card.
- [ ] Top services table.
- [ ] Doctor performance table.

Optional:

- [ ] Chart.js chart.
- [ ] Export CSV.

### 7.9 Audit log viewer

- [ ] Audit log list page.
- [ ] Filter by action.
- [ ] Filter by date.
- [ ] Show actor.
- [ ] Show entity type.
- [ ] Show entity ID.
- [ ] Show created time.

### 7.10 Deliverables của Member 2

- [ ] Web layout looks professional.
- [ ] Patient pages work.
- [ ] Doctor/specialty/room pages work.
- [ ] Doctor schedule pages work.
- [ ] Appointment pages work.
- [ ] Doctor consultation pages work.
- [ ] Dashboard displays clinic metrics.
- [ ] Audit log page displays critical actions.
- [ ] Web UI is ready for demo.

## 8. Member 3 - Nguyễn Hữu Tài: JavaFX UI + Billing/Payment tasks

### 8.1 Ownership chính

Tài phụ trách JavaFX desktop UI và billing/payment workflow. REST integration trong JavaFX sẽ do Member 1 nối chính.

Modules:

- JavaFX FXML screens.
- JavaFX controller event handling.
- Scene navigation.
- Front desk UI flow.
- Queue desktop screen.
- Billing backend support.
- Payment backend/UI.
- Invoice UI.

### 8.2 JavaFX UI structure

Suggested structure:

```text
desktop-client
├── controller
│   ├── LoginController.java
│   ├── TodayAppointmentController.java
│   ├── PatientSearchController.java
│   ├── WalkInController.java
│   ├── QueueBoardController.java
│   ├── PendingInvoiceController.java
│   └── PaymentController.java
├── service
│   ├── interfaces
│   └── impl
├── client
│   └── api clients implemented/integrated by Member 1
├── model
├── util
└── resources
    ├── fxml
    ├── css
    └── images
```

### 8.3 JavaFX screens

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

Rules:

- FXML controller only handles UI event and updates view.
- Use JavaFX `Task` or `Service` when calling backend through service layer to avoid UI freeze.
- Show `ProgressIndicator` when loading.
- Use JavaFX Alert/Dialog for success/error/confirm.
- CSS in `resources/css`, no inline style.
- Highlight invalid fields using CSS class.

### 8.4 REST integration boundary

Member 3 does:

- [ ] Prepare FXML screens.
- [ ] Prepare controller methods.
- [ ] Prepare UI table columns.
- [ ] Prepare form validation at UI level.
- [ ] Call JavaFX service interfaces.
- [ ] Coordinate with Member 1 on required DTO fields.

Member 1 does:

- [ ] Implement actual HTTP client.
- [ ] Implement auth handling.
- [ ] Implement API request/response mapping.
- [ ] Connect UI service implementation to REST API.
- [ ] Handle API error response mapping.

This boundary avoids duplicate REST clients and keeps API contract controlled by leader.

### 8.5 Billing module

Billing flow:

```text
Completed Encounter
-> Generate Invoice
-> Add Invoice Items
-> Calculate Total
-> Issue Invoice
-> Pay Invoice
```

Invoice status:

```text
DRAFT
-> ISSUED
-> PAID

DRAFT / ISSUED -> CANCELLED
```

Tasks:

- [ ] Implement invoice entity/repository/service with leader review.
- [ ] Generate invoice from completed visit.
- [ ] Add consultation fee.
- [ ] Add encounter service orders.
- [ ] Calculate subtotal and total.
- [ ] Prevent duplicate invoice for same visit.
- [ ] Prevent editing paid invoice.
- [ ] Provide pending invoice data for JavaFX screen.

### 8.6 Payment module

Payment methods:

- `CASH`
- `BANK_TRANSFER`
- `CARD`
- `MOMO_MOCK`

Tasks:

- [ ] View pending invoices.
- [ ] View invoice detail.
- [ ] Record payment.
- [ ] Store payment method.
- [ ] Store payment transaction.
- [ ] Mark invoice as `PAID`.
- [ ] Display payment result in JavaFX.

Business rules:

- [ ] Only `ISSUED` invoice can be paid.
- [ ] Payment amount must equal invoice total.
- [ ] Paid invoice cannot be modified.
- [ ] One invoice has one successful full payment in MVP.

### 8.7 Deliverables của Member 3

- [ ] JavaFX app opens successfully.
- [ ] JavaFX login screen is complete.
- [ ] Today appointment screen is complete.
- [ ] Patient search screen is complete.
- [ ] Check-in UI flow is complete.
- [ ] Queue board desktop screen is complete.
- [ ] Invoice generation works.
- [ ] Pending invoice screen works.
- [ ] Payment screen works.
- [ ] Paid invoice updates correctly.
- [ ] JavaFX app is ready for demo after Member 1 connects REST.

## 9. Core entities cần chốt sớm

Leader nên chốt các entity này trước khi mọi người code:

- `User`
- `Role`
- `Staff`
- `Patient`
- `Doctor`
- `Specialty`
- `Room`
- `DoctorAvailability`
- `Appointment`
- `QueueItem`
- `Visit`
- `Encounter`
- `ServiceCatalog`
- `EncounterService`
- `Invoice`
- `InvoiceItem`
- `Payment`
- `AuditLog`

Relationship gợi ý:

```text
User N-N Role
User 1-0..1 Staff
Staff 1-0..1 Doctor
Doctor N-1 Specialty
Doctor N-1 Room

Patient 1-N Appointment
Doctor 1-N Appointment
Appointment 0..1-1 QueueItem

Patient 1-N Visit
Appointment 0..1-1 Visit
QueueItem 1-0..1 Visit
Visit 1-1 Encounter

Encounter 1-N EncounterService
ServiceCatalog 1-N EncounterService

Visit 1-0..1 Invoice
Invoice 1-N InvoiceItem
Invoice 1-N Payment

User 1-N AuditLog
```

## 10. Milestones đề xuất

### Milestone 1 - Foundation

Owner: Member 1, support by all.

- [ ] Spring Boot project setup.
- [ ] SQL Server setup.
- [ ] Security skeleton.
- [ ] Seed data.
- [ ] Base web layout.
- [ ] JavaFX skeleton.
- [ ] API contract draft.

### Milestone 2 - Clinic master data

Owner: Member 1 + Member 2.

- [ ] Patient.
- [ ] Doctor.
- [ ] Specialty.
- [ ] Room.
- [ ] Doctor availability.

### Milestone 3 - Appointment and queue

Owner: all members.

- [ ] Appointment booking.
- [ ] Appointment validation.
- [ ] Check-in.
- [ ] Walk-in visit.
- [ ] Queue board.
- [ ] JavaFX appointment screen.
- [ ] JavaFX queue screen.

### Milestone 4 - Consultation and billing

Owner: Member 2 + Member 3, reviewed by Member 1.

- [ ] Encounter.
- [ ] Service orders.
- [ ] Invoice.
- [ ] Payment.

### Milestone 5 - Reports, audit, testing, demo

Owner: all members.

- [ ] Dashboard.
- [ ] Audit log.
- [ ] Business rule tests.
- [ ] Postman collection.
- [ ] Desktop test plan.
- [ ] README.
- [ ] Screenshots.
- [ ] Demo script.

## 11. One-week execution plan

### Day 1 - Foundation

Member 1:

- [ ] Backend project setup.
- [ ] SQL Server config.
- [ ] Security skeleton.
- [ ] User/Role seed data.
- [ ] API response/error format.

Member 2:

- [ ] Thymeleaf base layout.
- [ ] Sidebar/header.
- [ ] Patient UI mock pages.

Member 3:

- [ ] JavaFX project setup.
- [ ] FXML structure.
- [ ] Login screen mock.

### Day 2 - Master data

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

- [ ] JavaFX navigation.
- [ ] Patient search UI.
- [ ] Today appointment UI mock.

### Day 3 - Appointment

Member 1:

- [ ] Appointment create logic.
- [ ] Appointment reschedule/cancel.
- [ ] Prevent duplicate doctor slot.
- [ ] Validate doctor availability.
- [ ] REST endpoints for appointment.

Member 2:

- [ ] Appointment list page.
- [ ] Create appointment form.
- [ ] Reschedule/cancel UI.
- [ ] Today appointments page.

Member 3:

- [ ] Today appointments JavaFX screen.
- [ ] TableView and filters.
- [ ] Check-in button UI.

### Day 4 - Check-in and queue

Member 1:

- [ ] Check-in backend.
- [ ] Queue item generation.
- [ ] Queue status flow.
- [ ] Queue REST API.
- [ ] JavaFX appointment/queue REST integration.

Member 2:

- [ ] Queue web board.
- [ ] Check-in button on web.
- [ ] Queue filter by doctor/room.

Member 3:

- [ ] JavaFX check-in UI.
- [ ] JavaFX queue board.
- [ ] Loading/error states.

### Day 5 - Consultation and billing

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

### Day 6 - Reports, audit, polish

Member 1:

- [ ] Audit log backend.
- [ ] Core business rule tests.
- [ ] Security tests.
- [ ] Integration review.
- [ ] Postman collection core cases.

Member 2:

- [ ] Dashboard UI.
- [ ] Audit log viewer.
- [ ] UI polish.
- [ ] Form validation display.

Member 3:

- [ ] JavaFX UI polish.
- [ ] Payment flow polish.
- [ ] Error dialog polish.
- [ ] Desktop test cases.

### Day 7 - Final integration and demo

All:

- [ ] Full workflow manual test.
- [ ] Fix integration bugs.
- [ ] Prepare demo data.
- [ ] Prepare README.
- [ ] Add screenshots.
- [ ] Prepare demo script.
- [ ] Final code review.

## 12. MVP must-have

Nếu thiếu thời gian, phải ưu tiên các phần này:

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
- [ ] Basic audit log.
- [ ] Postman collection with at least 20 requests.
- [ ] Desktop test plan with at least 10 test cases.

## 13. Optional features

Chỉ làm nếu MVP đã ổn:

- [ ] Prescription.
- [ ] Medicine inventory.
- [ ] Notification.
- [ ] PDF invoice.
- [ ] Excel/PDF report export.
- [ ] Advanced chart.
- [ ] WebSocket real-time queue.
- [ ] Multi-branch clinic.
- [ ] Insurance claim.

## 14. Đánh giá: project đã ổn để triển khai chưa?

Kết luận: ý tưởng và cách chia workflow đã ổn để bắt đầu triển khai, nhưng chưa nên để từng member code tự do ngay. Cần leader chốt foundation trước, nếu không khi ghép web, backend và JavaFX rất dễ vỡ ở entity, enum, API contract và security.

Điểm mạnh hiện tại:

- Domain phòng khám phù hợp requirement môn học.
- Có cả web Thymeleaf và JavaFX desktop.
- Có workflow end-to-end rõ ràng, không chỉ CRUD.
- Có đủ nghiệp vụ để demo tốt: appointment, queue, encounter, invoice, payment.
- Có phân vai theo ownership hợp lý.

Điểm cần chỉnh trước khi triển khai:

- Đổi database target sang SQL Server để khớp requirement.
- Chốt package structure theo yêu cầu môn học.
- Chốt API prefix `/api/v1`.
- Chốt entity relationship trước khi chia code.
- Chốt enum status flow trước khi làm UI.
- Chốt common response/error format để JavaFX dễ xử lý.
- Chốt role names và security matrix.
- Tạo seed data sớm để các member test cùng một dữ liệu.
- Tạo Postman collection từ đầu, không đợi cuối kỳ.
- Tách rõ JavaFX UI và JavaFX REST integration như đã phân công.

## 15. Leader checklist trước khi giao task cho team

Leader nên hoàn thành hoặc ít nhất draft các mục này đầu tiên:

- [ ] Create repository structure.
- [ ] Add Spring Boot Maven project.
- [ ] Add SQL Server connection config.
- [ ] Add base entity and audit fields.
- [ ] Add global exception handling.
- [ ] Add common API response.
- [ ] Add security skeleton.
- [ ] Add seed users and roles.
- [ ] Create initial ERD or entity relationship document.
- [ ] Create enum status document.
- [ ] Create API contract document.
- [ ] Create sample Thymeleaf layout entry point for Member 2.
- [ ] Create JavaFX skeleton/API client interface for Member 3.
- [ ] Create GitHub issues for each member.
- [ ] Create branch naming convention.
- [ ] Create PR checklist.

Sau checklist này, Bảo và Tài có thể làm song song mà ít bị block.

## 16. Definition of Done

Một task chỉ được xem là xong khi:

- [ ] Entity/repository implemented if needed.
- [ ] Service interface and implementation completed.
- [ ] Business rules validated in service.
- [ ] Controller/API implemented.
- [ ] DTO/request/response used correctly.
- [ ] Error handling works.
- [ ] Role permission checked.
- [ ] UI connected if required.
- [ ] Manual test completed.
- [ ] Demo data works.
- [ ] No inline CSS in Thymeleaf.
- [ ] JavaFX controller does not contain business logic.

## 17. Final demo flow

Demo nên đi theo luồng này:

1. Admin logs in.
2. Admin creates specialty, room, doctor and schedule.
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
17. Admin views audit logs.

## 18. Git workflow

Branches:

- `main`: stable final version.
- `develop`: integration branch.
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

Mỗi member nên có ít nhất 3 feature/bugfix branches để đúng tinh thần làm nhóm với GitHub.

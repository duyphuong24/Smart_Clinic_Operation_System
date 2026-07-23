# GitHub Issues - Core Professional Issues

This file is the local issue catalog used for branch names, commit messages, and GitHub issue creation. Issue numbers are stable references for this project.

**Issue tracking rule:** When a project bug, feature bug, blocker, or missing requirement is found, add a new issue to this file using the next available issue number. Do not reuse existing numbers. Use that issue number in the branch name, commit context, and PR description when implementing the fix.

## Epic 01 - Project Foundation

### Issue 1: Initialize Spring Boot Backend Project

**Title:** Initialize Spring Boot backend with modular package structure

**Description:**
Set up the Spring Boot backend project with a clean modular structure to support clinic operations, web MVC, REST APIs, security, persistence, and shared business services.

**Acceptance Criteria:**
- Spring Boot project is created with Java 21+.
- SQL Server dependency is configured.
- Spring Data JPA, Spring Security, Thymeleaf, Validation, and Lombok are added.
- Package structure is organized by domain module.
- Application starts successfully.
- Health check endpoint or basic home page is available.

**Labels:** backend, foundation, priority:high

### Issue 2: Configure SQL Server Local Database And Demo Seed

**Title:** Configure SQL Server database connection and demo seed data

**Description:**
Set up a reproducible local SQL Server database configuration and prepare seed data for MVP demo users and roles. Flyway/Docker Compose can be added later if the MVP is stable.

**Acceptance Criteria:**
- Spring Boot connects to SQL Server through `application.properties`.
- SQL Server JDBC driver is configured.
- Local environment variables can override DB connection settings.
- Demo roles are seeded.
- Demo users are seeded for all required roles.

**Labels:** backend, database, devops, priority:high

### Issue 3: Implement Global Exception Handling and API Response Format

**Title:** Implement global exception handler and standardized API response format

**Description:**
Create a consistent error handling mechanism for REST APIs and web operations, including validation errors, business rule violations, unauthorized access, and not found resources.

**Acceptance Criteria:**
- Global exception handler is implemented.
- Validation errors return field-level messages.
- Business exceptions return clear messages.
- REST responses follow a consistent structure.
- Web pages show user-friendly error messages where applicable.

**Labels:** backend, api, quality, priority:high

## Epic 02 - Authentication & Authorization

### Issue 4: Implement User, Role, and RBAC Model

**Title:** Implement user, role, and permission-based access model

**Description:**
Create the authentication and authorization data model for clinic staff roles including Admin, Receptionist, Doctor, Cashier, and Manager.

**Acceptance Criteria:**
- User and Role entities are implemented.
- Many-to-many user-role relationship is configured.
- Default roles are seeded.
- Demo users are seeded for each role.
- User status supports ACTIVE and LOCKED.

**Labels:** backend, security, database, priority:high

### Issue 5: Implement Spring Security Login and Role-Based Access

**Title:** Implement Spring Security login and role-based page/API access

**Description:**
Secure the application using Spring Security. Users should access only the pages and APIs allowed by their roles.

**Acceptance Criteria:**
- Login and logout work correctly.
- Passwords are stored using BCrypt.
- Web routes are protected by role.
- REST APIs are protected by JWT and role rules.
- Access denied response/page is implemented.
- Unauthorized users cannot access restricted resources.

**Labels:** backend, security, priority:high

## Epic 03 - Patient Registry

### Issue 6: Implement Patient Registration and Profile Management

**Title:** Implement patient registration and profile management

**Description:**
Allow receptionists and admins to create and manage patient profiles, including personal information, contact details, emergency contact, and allergy notes.

**Acceptance Criteria:**
- Receptionist can create a new patient.
- Receptionist can update patient information.
- Patient code is generated automatically.
- Required fields are validated.
- Patient profile page displays basic information and visit history placeholder.

**Labels:** backend, web, patient, priority:high

### Issue 7: Implement Patient Search

**Title:** Implement patient search by code, name, and phone number

**Description:**
Provide a fast search function so receptionists and doctors can find patient records during appointment booking, check-in, and consultation.

**Acceptance Criteria:**
- Search supports patient code, name, and phone number.
- Search result is paginated.
- Search is available in Thymeleaf web UI.
- Search API is available for JavaFX desktop client.
- Empty result state is handled properly.

**Labels:** backend, web, api, patient, priority:high

## Epic 04 - Doctor & Schedule Management

### Issue 8: Implement Doctor, Specialty, and Room Management

**Title:** Implement doctor, specialty, and room management

**Description:**
Allow admins to manage doctor profiles, specialties, consultation fees, and clinic rooms.

**Acceptance Criteria:**
- Admin can create and update specialties.
- Admin can create doctor profile linked to staff user.
- Admin can assign specialty and consultation fee to doctor.
- Admin can manage clinic rooms.
- Doctor list and detail pages are available.

**Labels:** backend, web, doctor, priority:high

### Issue 9: Implement Doctor Availability Schedule

**Title:** Implement doctor availability schedule and validation service

**Description:**
Allow admins to configure doctor working hours and use this schedule to validate appointment booking.

**Acceptance Criteria:**
- Admin can define doctor availability by weekday.
- Availability includes start time, end time, room, and slot duration.
- Appointment service can validate whether a requested time is inside availability.
- Invalid appointment time is rejected with clear error message.
- Availability list is displayed in doctor detail page.

**Labels:** backend, schedule, doctor, priority:high

## Epic 05 - Appointment Management

### Issue 10: Implement Appointment Booking

**Title:** Implement appointment booking workflow

**Description:**
Allow receptionists to book appointments for patients with doctors based on doctor availability and slot conflicts.

**Acceptance Criteria:**
- Receptionist can create an appointment for a patient.
- Appointment requires patient, doctor, scheduled time, and reason.
- System validates doctor availability.
- System prevents duplicate booking for the same doctor and time slot.
- Appointment status is set to BOOKED after creation.

**Labels:** backend, web, appointment, priority:critical

### Issue 11: Implement Appointment List, Reschedule, and Cancellation

**Title:** Implement appointment listing, rescheduling, and cancellation

**Description:**
Allow clinic staff to view daily appointments, reschedule valid appointments, and cancel appointments with reason.

**Acceptance Criteria:**
- Users can view appointments by date.
- Receptionist can reschedule BOOKED appointments.
- Receptionist can cancel BOOKED appointments.
- Cancelled appointments cannot be checked in.
- Appointment status is displayed clearly in UI.

**Labels:** backend, web, appointment, priority:high

## Epic 06 - Check-in & Queue Management

### Issue 12: Implement Patient Check-in from Appointment

**Title:** Implement patient check-in from appointment

**Description:**
Allow receptionist to check in a booked appointment and create a queue item for the patient.

**Acceptance Criteria:**
- Receptionist can check in a BOOKED appointment.
- Appointment status changes to CHECKED_IN.
- Queue item is created automatically.
- Queue number is generated.
- Duplicate check-in is prevented.

**Labels:** backend, queue, appointment, priority:critical

### Issue 13: Implement Walk-in Visit Creation

**Title:** Implement walk-in patient visit and queue creation

**Description:**
Allow receptionist to create a walk-in visit for patients without prior appointment.

**Acceptance Criteria:**
- Receptionist can select or create patient.
- Receptionist can assign doctor and priority.
- Queue item is created without appointment.
- Queue number is generated correctly.
- Walk-in visit is visible in today queue.

**Labels:** backend, queue, patient, priority:high

### Issue 14: Implement Queue Board and Doctor Call Next Patient

**Title:** Implement clinic queue board and doctor call-next workflow

**Description:**
Create a queue board for receptionists and doctors to track waiting patients and allow doctors to call the next patient.

**Acceptance Criteria:**
- Queue board displays today waiting patients.
- Queue can be filtered by doctor or room.
- Doctor can call next patient.
- Queue status changes from WAITING to CALLED.
- Called patient can be moved to IN_SERVICE.
- Completed patient is removed from active queue.

**Labels:** backend, web, queue, priority:critical

## Epic 07 - Encounter / Consultation

### Issue 15: Implement Doctor Consultation Encounter

**Title:** Implement doctor consultation encounter workflow

**Description:**
Allow doctors to open a visit, record clinical information, diagnosis, notes, and complete the consultation.

**Acceptance Criteria:**
- Doctor can open a patient visit from queue.
- Encounter is created when consultation starts.
- Doctor can record chief complaint, diagnosis, and clinical note.
- Doctor can complete encounter.
- Completed encounter becomes read-only by default.

**Labels:** backend, web, encounter, priority:critical

### Issue 16: Implement Encounter Service Orders

**Title:** Implement service ordering inside doctor encounter

**Description:**
Allow doctors to add billable clinical services during consultation, such as consultation fee, lab test, procedure, or medication package.

**Acceptance Criteria:**
- Doctor can add service items to encounter.
- Service item uses price from service catalog.
- Quantity and note are supported.
- Encounter service items are included in invoice generation.
- Cancelled service items are not billed.

**Labels:** backend, billing, encounter, priority:high

## Epic 08 - Service Catalog, Billing & Payment

### Issue 17: Implement Service Catalog Management

**Title:** Implement clinic service catalog management

**Description:**
Allow admins to manage clinic services, categories, prices, and active status.

**Acceptance Criteria:**
- Admin can create service.
- Admin can update service price.
- Admin can deactivate service.
- Services can be filtered by type.
- Active services are selectable during encounter.

**Labels:** backend, web, billing, priority:high

### Issue 18: Implement Invoice Generation from Visit

**Title:** Implement invoice generation from completed visit

**Description:**
Generate invoice based on consultation fee and encounter service items after doctor completes the consultation.

**Acceptance Criteria:**
- Cashier can generate invoice for completed encounter.
- Invoice includes consultation fee and service items.
- Total amount is calculated correctly.
- Invoice status is ISSUED after generation.
- Duplicate invoice for the same visit is prevented.

**Labels:** backend, billing, priority:critical

### Issue 19: Implement Payment Processing

**Title:** Implement payment recording for issued invoices

**Description:**
Allow cashier to record payment for issued invoices using supported payment methods.

**Acceptance Criteria:**
- Cashier can mark invoice as paid.
- Payment method is recorded.
- Payment amount must match invoice total.
- Invoice status changes to PAID.
- Paid invoices cannot be edited.
- Payment transaction is stored.

**Labels:** backend, payment, billing, priority:critical

## Epic 09 - JavaFX Desktop Client

### Issue 20: Initialize JavaFX Desktop Client

**Title:** Initialize JavaFX desktop client with login and REST API integration

**Description:**
Set up the JavaFX desktop application as a front-desk client that communicates with the Spring Boot backend through REST APIs.

**Acceptance Criteria:**
- JavaFX project is initialized.
- FXML-based structure is used.
- Login screen is implemented.
- REST client is configured.
- Authenticated user session is stored in memory.
- Failed login shows clear error message.

**Labels:** desktop, javafx, api, priority:high

### Issue 21: Implement JavaFX Today Appointment and Check-in Screen

**Title:** Implement JavaFX today appointments and check-in workflow

**Description:**
Allow receptionists to view today appointments and check in patients directly from the desktop app.

**Acceptance Criteria:**
- Desktop app displays today appointments.
- Receptionist can search patient appointments.
- Receptionist can check in a booked appointment.
- Queue number is displayed after check-in.
- UI refreshes after successful check-in.

**Labels:** desktop, javafx, appointment, queue, priority:critical

### Issue 22: Implement JavaFX Queue and Payment Screens

**Title:** Implement JavaFX queue board and payment screen

**Description:**
Provide front-desk and cashier operations in the desktop app, including queue monitoring and invoice payment.

**Acceptance Criteria:**
- Desktop app displays active queue.
- Queue can be filtered by doctor.
- Cashier can view issued invoices.
- Cashier can record payment.
- Paid invoice status updates correctly.

**Labels:** desktop, javafx, queue, billing, priority:high

## Epic 10 - Reporting & Audit

### Issue 23: Implement Manager Dashboard

**Title:** Implement manager dashboard with clinic operational metrics

**Description:**
Create a dashboard for managers and admins to monitor daily clinic performance.

**Acceptance Criteria:**
- Dashboard shows today appointments.
- Dashboard shows waiting queue count.
- Dashboard shows completed visits.
- Dashboard shows today revenue.
- Dashboard shows top services.
- Access is restricted to Admin and Manager.

**Labels:** backend, web, report, priority:high

### Issue 24: Implement Audit Logging for Critical Actions

**Title:** Implement audit logging for critical clinic operations

**Description:**
Record important user actions to improve traceability and accountability across the system.

**Acceptance Criteria:**
- Creating appointment is audited.
- Checking in patient is audited.
- Starting/completing encounter is audited.
- Generating invoice is audited.
- Recording payment is audited.
- Admin can view audit logs.

**Labels:** backend, audit, security, priority:high

## Epic 11 - Quality & Demo

### Issue 25: Add Validation and Business Rule Tests

**Title:** Add validation and business rule tests for core clinic workflow

**Description:**
Implement tests for the most important business rules to ensure the system is stable for demo and future development.

**Acceptance Criteria:**
- Appointment cannot be booked outside doctor availability.
- Duplicate appointment slot is rejected.
- Cancelled appointment cannot be checked in.
- Completed encounter cannot be edited.
- Paid invoice cannot be modified.
- Unauthorized role cannot access restricted operation.

**Labels:** testing, backend, quality, priority:high

### Issue 26: Prepare Demo Data, README, and Architecture Documentation

**Title:** Prepare demo dataset, README, and project documentation

**Description:**
Create professional documentation and sample data so the project can be presented clearly and added to CV/GitHub portfolio.

**Acceptance Criteria:**
- README includes project overview.
- README includes tech stack.
- README includes architecture diagram.
- README includes setup instructions.
- Sample users and sample clinic data are seeded.
- Demo script is documented.
- Screenshots are added.

**Labels:** documentation, demo, priority:high

### Issue 27: JWT Access and Refresh Token Database Storage & Rotation

**Title:** Upgrade JWT Security with Refresh Token Database Storage & Rotation

**Description:**
Implement database-backed refresh tokens for JWT rotation, storing hashes in database with unique constraints, enforcing SecureRandom token generation, and revoking active sessions on reuse detection or logout.

**Acceptance Criteria:**
- Refresh token is generated using cryptographically strong SecureRandom.
- Only SHA-256 hashes of refresh tokens are stored in the database.
- Refresh token database table has unique constraints and indexes.
- Rotation reuse blocks active sessions and revokes refresh tokens.
- Access token is strictly stateless and not stored in the database.

**Labels:** security, backend, priority:high

### Issue 28: Security Config Role Matrix and Audit Logs Alignment

**Title:** Align Spring Security config with security-matrix.md and secure audit logs

**Description:**
Align endpoint matchers in SecurityConfig 100% with security-matrix.md, ensuring MANAGER can read invoices, payments, services, and restrict /api/v1/audit-logs to ADMIN and MANAGER.

**Acceptance Criteria:**
- /api/v1/audit-logs/** is restricted to ADMIN and MANAGER.
- MANAGER role is granted read-only GET access to invoices, payments, and services.
- RECEPTIONIST and other unauthorized roles receive 403 Forbidden on audit logs.

**Labels:** security, backend, priority:high

### Issue 30: Compliance Gap Fixes for HSF302 Requirements

**Title:** Implement missing requirements: Postman Collection, CSS modules, PR Template, Optimistic Locking, Login Attempt Lock, and JaCoCo coverage

**Description:**
Address the compliance gap items identified against HSF302_Project_Requirements.docx including Postman collection, module CSS files, GitHub PR template, Optimistic Locking (@Version), account lock after 5 failed login attempts, JaCoCo coverage plugin, and desktop test plan.

**Acceptance Criteria:**
- Postman Collection with ≥20 requests and environment file is created in postman/ directory.
- Module-specific CSS files are separated in static/css/.
- GitHub PR template is created under .github/pull_request_template.md.
- Optimistic locking (@Version) is added to core entities.
- User entity & auth service support failed login counting and 30-minute lock after 5 failures.
- JaCoCo plugin is configured in pom.xml.
- Desktop test plan and demo script are documented.

**Labels:** compliance, backend, frontend, documentation, priority:high

### Issue 31: Fix Dashboard Null Pointer Defect & Implement Web Thymeleaf Payment, Reports, and Audit Logs UI

**Title:** Fix Dashboard primitive null mapping error, optimize metrics query, and implement Web Thymeleaf Payment Recording, Reports, and Audit Logs views

**Description:**
Fix the runtime 500 error on /dashboard caused by null boolean mapping in Appointment entity, optimize dashboard aggregation to use ReportService, add POST payment recording endpoint and modal in Billing module, and build Web MVC controllers and Thymeleaf templates for /reports and /audit-logs.

**Acceptance Criteria:**
- Appointment.reminderSent is converted to a null-safe Boolean wrapper class with helper getter.
- AuthController.dashboard() delegates to ReportService.dashboardMetrics() to prevent OOM.
- BillingController supports POST /billing/invoices/{id}/payments to record cashier payments from Web forms.
- billing/detail.html includes Payment History table and Record Payment modal dialog.
- ReportController and report/dashboard.html are created to display KPI metrics and revenue.
- AuditLogController and admin/audit-log.html are created to display system audit trail.
- Sidebar menu in layout/main.html includes Reports & Analytics and Audit Logs links.
- All unit and integration tests pass 100%.

**Labels:** bugfix, feature, backend, frontend, priority:high

# Team Rules - Smart Clinic Operations System

This document defines the team working rules for the Smart Clinic Operations System project. The goal is to keep the codebase consistent, reviewable, and easy to integrate within a short one-week development timeline.

## 1. Core Principles

- Work by feature workflow, not by random CRUD tasks.
- One branch should focus on one feature, bug fix, or documentation task.
- One commit should describe one clear change.
- Do not push directly to `main` unless you are the leader handling an approved integration.
- Do not commit code that does not compile.
- Do not change shared database schema, entity relationships, enum status flow, or API contracts without informing the leader.
- Keep business logic in the service layer. Controllers should only receive input, call services, and return views/responses.
- JavaFX controllers should only handle UI events. They must not contain business logic or call HTTP clients directly.

## 2. Branch Naming Rules

Branch names must follow this format:

```text
<type>/<issue-id>-<short-description>
```

Allowed branch types:

```text
feature
bugfix
refactor
docs
test
chore
```

Examples without GitHub issues:

```text
feature/01-project-setup
feature/02-auth-rbac
feature/03-patient-management
feature/04-appointment-booking
feature/05-queue-management
feature/06-javafx-login
bugfix/07-fix-login-redirect
docs/08-update-api-contract
refactor/09-clean-service-layer
```

Examples with GitHub issues:

```text
feature/12-appointment-booking
bugfix/18-fix-duplicate-slot-validation
docs/21-update-demo-script
```

Branch type meaning:

| Type | Meaning |
| --- | --- |
| `feature` | New user-facing or business feature |
| `bugfix` | Bug fix |
| `refactor` | Code restructuring without behavior change |
| `docs` | Documentation update |
| `test` | Test additions or test fixes |
| `chore` | Build, dependency, config, cleanup |

## 3. Commit Message Rules

Use Conventional Commits.

Format:

```text
<type>(<scope>): <short description>
```

Allowed commit types:

```text
feat
fix
refactor
docs
test
style
chore
```

Common scopes:

```text
auth
user
patient
doctor
schedule
appointment
queue
encounter
billing
payment
javafx
api
db
security
docs
pom
```

Good examples:

```text
feat(auth): add role based login
feat(patient): add patient create form
feat(appointment): validate duplicate doctor slot
fix(queue): prevent check-in for cancelled appointment
refactor(service): extract appointment validation logic
docs(api): update appointment endpoint contract
test(appointment): add duplicate slot validation test
chore(pom): add spring security dependency
style(css): polish patient table layout
```

Bad examples:

```text
update code
fix bug
done task
commit lan 1
them chuc nang
```

Rules:

- Use English for commit messages.
- Use present tense: `add`, `fix`, `update`, not `added`, `fixed`, `updated`.
- Keep the first line short and specific.
- Do not mix unrelated changes in one commit.

## 4. Pull Request Rules

Pull request title format:

```text
[TYPE] Short description
```

Examples:

```text
[FEAT] Add authentication and RBAC
[FEAT] Add patient management pages
[FIX] Prevent duplicate appointment slots
[DOCS] Update API contract
```

Pull request description template:

```markdown
## Summary
- Describe what changed.

## Test
- Describe how this was tested.

## Notes
- Mention anything reviewers should know.
```

PR rules:

- Open PR into `main` unless the leader creates another integration branch.
- The leader should review business logic, schema changes, API changes, and security changes before merge.
- Resolve merge conflicts locally before requesting final review.
- Attach screenshots for UI changes when possible.

## 5. Git Workflow

Before starting work:

```bash
git checkout main
git pull origin main
```

Create a new branch:

```bash
git checkout -b feature/03-patient-management
```

Commit in small steps:

```bash
git add .
git commit -m "feat(patient): add patient entity and repository"
git commit -m "feat(patient): add patient service validation"
git commit -m "feat(patient): add patient web pages"
```

Push the branch:

```bash
git push origin feature/03-patient-management
```

Then create a pull request into `main`.

## 6. Package Naming Rules

Java packages must be lowercase.

Good:

```text
com.smartclinic.appointment
com.smartclinic.patient
com.smartclinic.billing
```

Bad:

```text
com.smartclinic.Appointment
com.smartclinic.patientManagement
```

Each main backend domain module should use this internal structure:

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

Examples:

```text
com.smartclinic.appointment.service
com.smartclinic.appointment.repository
com.smartclinic.appointment.dto
```

## 7. Java Class Naming Rules

Entity:

```text
Patient
Appointment
QueueItem
Invoice
Payment
```

Repository:

```text
PatientRepository
AppointmentRepository
QueueItemRepository
```

Service interface:

```text
PatientService
AppointmentService
QueueService
```

Service implementation:

```text
PatientServiceImpl
AppointmentServiceImpl
QueueServiceImpl
```

Thymeleaf MVC controller:

```text
PatientController
AppointmentController
QueueController
```

REST controller:

```text
PatientRestController
AppointmentRestController
QueueRestController
```

DTO:

```text
PatientCreateRequest
PatientUpdateRequest
PatientResponse
AppointmentCreateRequest
AppointmentResponse
```

Mapper:

```text
PatientMapper
AppointmentMapper
```

Validator:

```text
AppointmentValidator
QueueValidator
```

## 8. REST API Naming Rules

REST API prefix:

```text
/api/v1
```

Use plural resource names:

```text
/api/v1/patients
/api/v1/appointments
/api/v1/queue-items
/api/v1/invoices
```

Do not use verbs in resource URLs:

```text
/api/v1/getPatients
/api/v1/createAppointment
```

Use action endpoints only for workflow transitions:

```text
PATCH /api/v1/appointments/{id}/cancel
PATCH /api/v1/appointments/{id}/check-in
PATCH /api/v1/queue-items/{id}/call
PATCH /api/v1/queue-items/{id}/complete
POST  /api/v1/invoices/{id}/payments
```

## 9. Database Naming Rules

Table names use snake_case and plural names:

```text
users
roles
user_roles
patients
appointments
queue_items
doctor_availabilities
service_catalog
invoice_items
```

Column names use snake_case:

```text
full_name
date_of_birth
created_at
updated_at
scheduled_start
scheduled_end
```

Primary key column:

```text
id
```

Foreign key columns:

```text
patient_id
doctor_id
appointment_id
created_by
```

Index naming:

```text
idx_appointments_doctor_start
idx_appointments_patient
idx_queue_items_date_status
```

Unique constraint/index naming:

```text
uq_users_email
uq_patients_patient_code
uq_appointments_code
uq_queue_items_date_number
```

## 10. Files That Must Not Be Committed

Do not commit generated files or local secrets:

```text
target/
.idea/
*.iml
.env
.envi
*.log
```

Only commit example environment files, such as:

```text
.env.example
.envi_example
```

## 11. Minimum Checks Before Merge

Before opening or merging a PR, run at least:

```bash
cd clinic-backend
./mvnw test
```

On Windows PowerShell:

```powershell
cd clinic-backend
.\mvnw.cmd test
```

For JavaFX changes, also run:

```powershell
cd clinic-desktop
.\mvnw.cmd test
```

A task is not considered done until:

- The code compiles.
- The related workflow can be manually tested.
- The implementation follows the package and naming rules.
- The commit messages are clear.
- No local secret or generated file is included.
- The leader has reviewed schema, security, API, and workflow changes when applicable.
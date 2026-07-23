# Team Rules - Smart Clinic Operations System

This document defines the team working rules for the Smart Clinic Operations System project. The goal is to keep the codebase consistent, reviewable, and easy to integrate within a short one-week development timeline.

## 1. Core Principles

- Work by feature workflow, not by random CRUD tasks.
- One branch should focus on one feature, bug fix, or documentation task.
- One commit should describe one clear change.
- Do not push directly to `main` unless you are the leader handling an approved integration.
- Do not commit code that does not compile.
- Do not change shared database schema, entity relationships, enum status flow, or API contracts without informing the leader.
- When the project or a feature has a new bug, blocker, or missing requirement, add a new issue to the issue list before starting the fix.
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

Pull Request rules and template are defined in [.github/pull_request_template.md](.github/pull_request_template.md).

- All PRs must follow the automated template provided in `.github/pull_request_template.md`.
- PR titles must follow: `[TYPE] Short description` (e.g. `[FEAT] Add authentication and RBAC`, `[FIX] Prevent duplicate appointment slots`).
- Link related issues in the PR description (e.g., `Closes #12`).
- Ensure all unit & integration tests pass (`mvn test`) before requesting merge.

## 5. Issue Tracking Rules

Use `docs/issues.md` as the local source of truth for project issues.

When a project bug, feature bug, blocker, or missing requirement is found:

- Add a new issue entry to `docs/issues.md` before creating the fix branch.
- Use the next available issue number in the current issue list.
- Do not reuse old issue numbers, even if an old issue was closed or superseded.
- Write the issue title, description, acceptance criteria, and labels clearly enough for another member to implement or review.
- Use the new issue number in the branch name and PR description when the work is tied to that issue.

Example:

```text
Existing last issue: Issue 26
New production bug: Issue 27
Branch: bugfix/27-fix-appointment-checkin-status
Commit: fix(queue): prevent check-in from invalid appointment status
```

## 6. Git Workflow

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

## 7. Package Naming Rules

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

Business CRUD modules should use this internal structure when applicable:

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

Examples of business CRUD modules:

```text
user
staff
doctor
specialty
room
patient
appointment
queue
billing
payment
```

Technical modules use only the packages they need:

```text
auth      -> controller/rest/dto/service if authentication endpoints are needed
security  -> config/principal/service/util for Spring Security infrastructure
common    -> api/entity/exception/enums/util/validation for shared building blocks
config    -> application-level Spring configuration
```

Do not mix responsibilities:

- Do not put Spring Security infrastructure inside clinic business services.
- Do not put user account management inside `staff` or `doctor`.
- Do not put room/specialty CRUD inside `doctor`.

Examples:

```text
com.smartclinic.appointment.service
com.smartclinic.appointment.repository
com.smartclinic.appointment.dto
```

## 8. Java Class Naming Rules

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

## 9. REST API Naming Rules

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

## 10. Database Naming Rules

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
uq_users_user_name
uq_patients_patient_code
uq_appointments_code
uq_queue_items_date_number
```

## 11. Files That Must Not Be Committed

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

## 12. Minimum Checks Before Merge

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

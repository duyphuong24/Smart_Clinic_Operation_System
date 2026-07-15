# Database Design - Smart Clinic Operations System

This document defines the temporary MVP database design for the Smart Clinic Operations System. It is intended to unblock implementation for a one-week, three-member project. This is the agreed design baseline, not the final production migration script.

## 1. Design Scope

The MVP database supports this end-to-end clinic workflow:

```text
User login
-> Patient registration
-> Doctor schedule
-> Appointment booking
-> Patient check-in
-> Queue management
-> Doctor consultation
-> Invoice generation
-> Payment recording
-> Basic reporting/demo
```

`AuditLog` is intentionally excluded from the MVP critical path. It can be added later if the core workflow is stable.

## 2. Core Entities

| Group | Entities |
| --- | --- |
| Security and staff | `User`, `Role`, `UserRole`, `Staff`, `Doctor`, `Specialty`, `Room` |
| Patient and schedule | `Patient`, `DoctorAvailability`, `Appointment` |
| Queue and consultation | `QueueItem`, `Visit`, `Encounter`, `ServiceCatalog`, `EncounterService` |
| Billing and payment | `Invoice`, `InvoiceItem`, `Payment` |

## 3. Relationship Baseline

```text
User N-N Role
User 1-0..1 Staff
Staff 1-0..1 Doctor

Doctor N-1 Specialty
Doctor N-1 Room
Doctor 1-N DoctorAvailability
Room 1-N DoctorAvailability

Patient 1-N Appointment
Doctor 1-N Appointment
Room 1-N Appointment
User 1-N Appointment created_by

Appointment 0..1-1 QueueItem
Patient 1-N QueueItem
Doctor 1-N QueueItem
Room 1-N QueueItem

Patient 1-N Visit
Doctor 1-N Visit
Appointment 0..1-1 Visit
QueueItem 0..1-1 Visit

Visit 1-1 Encounter
Doctor 1-N Encounter

Encounter 1-N EncounterService
ServiceCatalog 1-N EncounterService

Visit 1-0..1 Invoice
Patient 1-N Invoice
User 1-N Invoice issued_by

Invoice 1-N InvoiceItem
ServiceCatalog 1-N InvoiceItem

Invoice 1-N Payment
User 1-N Payment paid_by
```

Important distinction:

- `Appointment` is a planned booking.
- `QueueItem` is a patient waiting in today's queue.
- `Visit` is the real clinical visit when consultation starts.

## 4. SQL Server Type Rules

| Concept | Use | Avoid |
| --- | --- | --- |
| Date and time | `DATETIME2` | `TIMESTAMP` |
| Boolean flag | `BIT` | `BOOLEAN` |
| Short text | `NVARCHAR(length)` | `VARCHAR` for user-facing text |
| Long text | `NVARCHAR(MAX)` | `TEXT` |
| Money/amount | `DECIMAL(12,2)` | `FLOAT` |
| Primary key | `BIGINT IDENTITY(1,1)` | Manual IDs |

Notes:

- In SQL Server, `TIMESTAMP` means `rowversion`; it is not a date-time type.
- Use `NVARCHAR` because patient names, notes, and addresses may contain Vietnamese text.
- Keep enum/status fields as `NVARCHAR(30)` or `NVARCHAR(50)` for MVP.

## 5. Table Baseline

Only add fields beyond this baseline after leader review.

### `users`

```text
id BIGINT PK
email NVARCHAR(150) UNIQUE NOT NULL
password_hash NVARCHAR(255) NOT NULL
full_name NVARCHAR(150) NOT NULL
phone NVARCHAR(30) NULL
status NVARCHAR(30) NOT NULL -- ACTIVE, LOCKED
created_at DATETIME2 NOT NULL
updated_at DATETIME2 NULL
version BIGINT NULL
```

### `roles`

```text
id BIGINT PK
name NVARCHAR(50) UNIQUE NOT NULL -- ADMIN, RECEPTIONIST, DOCTOR, CASHIER, MANAGER
```

### `user_roles`

```text
user_id BIGINT FK users
role_id BIGINT FK roles
PRIMARY KEY (user_id, role_id)
```

### `staff`

```text
id BIGINT PK
user_id BIGINT UNIQUE FK users
employee_code NVARCHAR(50) UNIQUE NOT NULL
staff_type NVARCHAR(50) NOT NULL -- RECEPTIONIST, DOCTOR, CASHIER, MANAGER
hired_date DATE NULL
status NVARCHAR(30) NOT NULL -- ACTIVE, INACTIVE
created_at DATETIME2 NOT NULL
updated_at DATETIME2 NULL
version BIGINT NULL
```

### `doctors`

```text
id BIGINT PK
staff_id BIGINT UNIQUE FK staff
specialty_id BIGINT FK specialties
default_room_id BIGINT NULL FK rooms
license_no NVARCHAR(100) UNIQUE NOT NULL
consultation_fee DECIMAL(12,2) NOT NULL
bio NVARCHAR(MAX) NULL
active BIT NOT NULL DEFAULT 1
created_at DATETIME2 NOT NULL
updated_at DATETIME2 NULL
version BIGINT NULL
```

### `specialties`

```text
id BIGINT PK
name NVARCHAR(100) UNIQUE NOT NULL
description NVARCHAR(MAX) NULL
active BIT NOT NULL DEFAULT 1
```

### `rooms`

```text
id BIGINT PK
room_code NVARCHAR(50) UNIQUE NOT NULL
name NVARCHAR(100) NOT NULL
floor NVARCHAR(30) NULL
active BIT NOT NULL DEFAULT 1
```

### `patients`

```text
id BIGINT PK
patient_code NVARCHAR(50) UNIQUE NOT NULL
full_name NVARCHAR(150) NOT NULL
date_of_birth DATE NULL
gender NVARCHAR(20) NULL -- MALE, FEMALE, OTHER
phone NVARCHAR(30) NULL
email NVARCHAR(150) NULL
address NVARCHAR(MAX) NULL
identity_number NVARCHAR(50) NULL
emergency_contact_name NVARCHAR(150) NULL
emergency_contact_phone NVARCHAR(30) NULL
allergy_note NVARCHAR(MAX) NULL
status NVARCHAR(30) NOT NULL -- ACTIVE, ARCHIVED
created_at DATETIME2 NOT NULL
updated_at DATETIME2 NULL
version BIGINT NULL
```

### `doctor_availabilities`

```text
id BIGINT PK
doctor_id BIGINT FK doctors
day_of_week INT NOT NULL -- 1 = Monday, 7 = Sunday
start_time TIME NOT NULL
end_time TIME NOT NULL
slot_minutes INT NOT NULL
room_id BIGINT FK rooms
active BIT NOT NULL DEFAULT 1
```

### `appointments`

```text
id BIGINT PK
appointment_code NVARCHAR(50) UNIQUE NOT NULL
patient_id BIGINT FK patients
doctor_id BIGINT FK doctors
room_id BIGINT FK rooms
scheduled_start DATETIME2 NOT NULL
scheduled_end DATETIME2 NOT NULL
reason NVARCHAR(MAX) NULL
source NVARCHAR(30) NOT NULL -- PHONE, WALK_IN, ONLINE
status NVARCHAR(30) NOT NULL -- BOOKED, CHECKED_IN, IN_CONSULTATION, COMPLETED, CANCELLED, NO_SHOW
created_by BIGINT FK users
cancelled_reason NVARCHAR(MAX) NULL
created_at DATETIME2 NOT NULL
updated_at DATETIME2 NULL
version BIGINT NULL
```

### `queue_items`

```text
id BIGINT PK
queue_number NVARCHAR(30) NOT NULL
queue_date DATE NOT NULL
patient_id BIGINT FK patients
doctor_id BIGINT FK doctors
room_id BIGINT FK rooms
appointment_id BIGINT NULL FK appointments
priority NVARCHAR(30) NOT NULL -- NORMAL, URGENT, ELDERLY
status NVARCHAR(30) NOT NULL -- WAITING, CALLED, IN_SERVICE, DONE, SKIPPED
checked_in_at DATETIME2 NOT NULL
called_at DATETIME2 NULL
completed_at DATETIME2 NULL
created_by BIGINT FK users
version BIGINT NULL
```

### `visits`

```text
id BIGINT PK
visit_code NVARCHAR(50) UNIQUE NOT NULL
patient_id BIGINT FK patients
doctor_id BIGINT FK doctors
appointment_id BIGINT NULL UNIQUE FK appointments
queue_item_id BIGINT NULL UNIQUE FK queue_items
status NVARCHAR(30) NOT NULL -- WAITING, IN_CONSULTATION, COMPLETED, CANCELLED
started_at DATETIME2 NULL
ended_at DATETIME2 NULL
created_at DATETIME2 NOT NULL
updated_at DATETIME2 NULL
version BIGINT NULL
```

### `encounters`

```text
id BIGINT PK
visit_id BIGINT UNIQUE FK visits
doctor_id BIGINT FK doctors
chief_complaint NVARCHAR(MAX) NULL
diagnosis NVARCHAR(MAX) NULL
clinical_note NVARCHAR(MAX) NULL
status NVARCHAR(30) NOT NULL -- OPEN, COMPLETED
started_at DATETIME2 NOT NULL
completed_at DATETIME2 NULL
created_at DATETIME2 NOT NULL
updated_at DATETIME2 NULL
version BIGINT NULL
```

### `service_catalog`

```text
id BIGINT PK
service_code NVARCHAR(50) UNIQUE NOT NULL
name NVARCHAR(150) NOT NULL
type NVARCHAR(50) NOT NULL -- CONSULTATION, LAB_TEST, PROCEDURE, MEDICINE
price DECIMAL(12,2) NOT NULL
active BIT NOT NULL DEFAULT 1
created_at DATETIME2 NOT NULL
updated_at DATETIME2 NULL
version BIGINT NULL
```

### `encounter_services`

```text
id BIGINT PK
encounter_id BIGINT FK encounters
service_id BIGINT FK service_catalog
quantity INT NOT NULL DEFAULT 1
unit_price DECIMAL(12,2) NOT NULL
note NVARCHAR(MAX) NULL
status NVARCHAR(30) NOT NULL -- ORDERED, COMPLETED, CANCELLED
```

### `invoices`

```text
id BIGINT PK
invoice_code NVARCHAR(50) UNIQUE NOT NULL
visit_id BIGINT UNIQUE FK visits
patient_id BIGINT FK patients
subtotal DECIMAL(12,2) NOT NULL
discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0
total_amount DECIMAL(12,2) NOT NULL
status NVARCHAR(30) NOT NULL -- ISSUED, PAID, CANCELLED
issued_by BIGINT FK users
issued_at DATETIME2 NOT NULL
paid_at DATETIME2 NULL
created_at DATETIME2 NOT NULL
updated_at DATETIME2 NULL
version BIGINT NULL
```

### `invoice_items`

```text
id BIGINT PK
invoice_id BIGINT FK invoices
service_id BIGINT NULL FK service_catalog
description NVARCHAR(255) NOT NULL
quantity INT NOT NULL
unit_price DECIMAL(12,2) NOT NULL
line_total DECIMAL(12,2) NOT NULL
```

### `payments`

```text
id BIGINT PK
invoice_id BIGINT FK invoices
amount DECIMAL(12,2) NOT NULL
method NVARCHAR(50) NOT NULL -- CASH, CARD, BANK_TRANSFER, MOMO_MOCK
status NVARCHAR(30) NOT NULL -- SUCCESS, FAILED
transaction_ref NVARCHAR(100) NULL
paid_by BIGINT FK users
paid_at DATETIME2 NOT NULL
```

## 6. Index and Unique Rules

Required unique constraints:

```text
uq_users_email
uq_roles_name
uq_staff_employee_code
uq_staff_user_id
uq_doctors_staff_id
uq_doctors_license_no
uq_specialties_name
uq_rooms_room_code
uq_patients_patient_code
uq_appointments_code
uq_queue_items_date_number
uq_visits_code
uq_visits_appointment_id
uq_visits_queue_item_id
uq_encounters_visit_id
uq_service_catalog_code
uq_invoices_code
uq_invoices_visit_id
```

Recommended indexes:

```text
idx_appointments_doctor_start
idx_appointments_patient
idx_appointments_status
idx_appointments_scheduled_start
idx_queue_items_date_status
idx_queue_items_doctor_date
idx_patients_phone
idx_invoices_status
```

Duplicate appointment slot rule:

```text
A doctor cannot have overlapping active appointments.
Active appointment statuses are BOOKED, CHECKED_IN, and IN_CONSULTATION.
CANCELLED and NO_SHOW appointments do not block a slot.
```

For MVP, enforce duplicate appointment prevention in the service layer first. Do not add a SQL Server filtered unique index until the service rule is stable.

## 7. MVP Exclusions

Do not implement these in the first week unless the core workflow is already stable:

```text
AuditLog
Prescription
Medicine inventory
Insurance claim
Refund workflow
Notification
PDF invoice
Advanced reporting
WebSocket realtime queue
Multi-branch clinic
```
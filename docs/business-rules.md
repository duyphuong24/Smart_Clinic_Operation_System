# Business Rules - Smart Clinic Operations System

This document is the MVP business rule source of truth for backend services, Thymeleaf actions, and JavaFX REST integration.

## 1. General Architecture Rules

- Controllers only receive input, call services, and return views or API responses.
- REST controllers and Thymeleaf controllers must call the same service layer.
- JavaFX controllers only handle UI events and must not call HTTP clients directly.
- Business validation belongs in backend services.
- Write operations should be transactional at service layer.
- REST responses should use the standard `ApiResponse` format.

## 2. Authentication And Security Rules

- Login uses `userName` and password.
- Passwords must be stored as BCrypt hashes, never plain text.
- Only users with status `ACTIVE` can login.
- Users with status `LOCKED` must be rejected by Spring Security.
- Roles are stored as `ADMIN`, `RECEPTIONIST`, `DOCTOR`, `CASHIER`, `MANAGER` and mapped to `ROLE_*` authorities.
- API clients must send JWT Bearer token after JWT is implemented.
- Security failures return `401` for unauthenticated and `403` for authenticated but unauthorized.

## 3. Patient Rules

- Patient code must be unique.
- Patient full name is required.
- Patient phone should be searchable.
- Archived patients should not be selected for new appointment booking unless explicitly reactivated.
- Patient medical notes such as allergy note are informational in MVP and do not block booking.

## 4. Doctor, Specialty, Room, And Schedule Rules

- Specialty name must be unique.
- Room code must be unique.
- Doctor license number must be unique.
- Doctor must belong to one staff profile.
- Doctor availability must have `start_time < end_time`.
- Appointment booking must be inside doctor availability.
- Inactive doctors, rooms, specialties, or availabilities must not be used for new booking.

## 5. Appointment Rules

- Appointment status flow must follow `status-flow.md`.
- Only `BOOKED` appointments can be checked in.
- Cancelled appointments cannot be checked in.
- Completed appointments cannot be rescheduled or cancelled.
- `scheduled_start` must be before `scheduled_end`.
- A doctor cannot have overlapping active appointments.
- Active appointment statuses are `BOOKED`, `CHECKED_IN`, and `IN_CONSULTATION`.
- `CANCELLED` and `NO_SHOW` appointments do not block a doctor slot.
- Duplicate slot prevention is enforced in service layer for MVP.

## 6. Queue Rules

- Check-in creates one queue item for one appointment.
- One appointment can create at most one queue item.
- Walk-in patients can create queue items without appointment.
- Queue number must be unique per queue date.
- Active queue excludes `DONE` and `SKIPPED` items.
- Doctor can only call patients assigned to that doctor.
- Queue status flow is `WAITING -> CALLED -> IN_SERVICE -> DONE`.
- `WAITING` or `CALLED` can become `SKIPPED`.

## 7. Visit And Encounter Rules

- Visit represents the real clinical visit.
- One appointment can have at most one visit.
- One queue item can have at most one visit.
- One visit can have at most one encounter.
- Encounter starts as `OPEN` and can only move to `COMPLETED`.
- A completed encounter is read-only in MVP.
- Only the assigned doctor can complete the encounter.
- Invoice generation requires a completed encounter.

## 8. Service Catalog And Encounter Service Rules

- Service code must be unique.
- Service price uses `BigDecimal`, not `double`.
- Inactive services cannot be added to new encounters.
- Encounter service quantity must be greater than zero.
- Encounter service stores a snapshot `unit_price` at order time.
- Cancelled encounter services are not billable.
- For MVP, ordered services may be treated as billable unless cancelled.

## 9. Invoice Rules

- Invoice status flow is `ISSUED -> PAID` or `ISSUED -> CANCELLED`.
- `DRAFT` is excluded from MVP.
- One visit can have at most one invoice.
- Invoice can only be generated after encounter completion.
- Invoice items are generated from consultation fee and billable encounter services.
- Paid invoices cannot be modified or cancelled.
- Cancelled invoices cannot be paid.
- Total amount equals subtotal minus discount amount.

## 10. Payment Rules

- Only `ISSUED` invoices can be paid.
- Payment amount must equal invoice total in MVP.
- One invoice has one successful full payment in MVP.
- Successful payment changes invoice status to `PAID` and sets `paid_at`.
- Failed payment does not change invoice status.
- Refund workflow is excluded from MVP.

## 11. Reporting And Demo Rules

- Dashboard metrics are read-only.
- Demo data must support the full flow from login to payment.
- Demo should use stable seed accounts and manually prepared clinic data.
- Optional features such as audit log, prescription, PDF invoice, notification, refund, and WebSocket queue are excluded until MVP is stable.

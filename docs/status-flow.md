# Status Flow - Smart Clinic Operations System

This document defines the temporary MVP status flow for appointment, queue, encounter, invoice, and payment workflows. All backend services, Thymeleaf actions, and JavaFX screens must follow these transitions.

## 1. AppointmentStatus

Allowed statuses:

```text
BOOKED
CHECKED_IN
IN_CONSULTATION
COMPLETED
CANCELLED
NO_SHOW
```

Main flow:

```text
BOOKED -> CHECKED_IN -> IN_CONSULTATION -> COMPLETED
```

Alternative flows:

```text
BOOKED -> CANCELLED
BOOKED -> NO_SHOW
```

Invalid transitions:

```text
CANCELLED -> CHECKED_IN
CANCELLED -> IN_CONSULTATION
CANCELLED -> COMPLETED
COMPLETED -> CANCELLED
COMPLETED -> CHECKED_IN
NO_SHOW -> CHECKED_IN
```

Business rules:

- Only `BOOKED` appointments can be checked in.
- A cancelled appointment cannot be checked in.
- A completed appointment cannot be rescheduled.
- Appointment booking must be inside doctor availability.
- A doctor cannot have overlapping active appointments.
- Active appointment statuses are `BOOKED`, `CHECKED_IN`, and `IN_CONSULTATION`.

## 2. QueueStatus

Allowed statuses:

```text
WAITING
CALLED
IN_SERVICE
DONE
SKIPPED
```

Main flow:

```text
WAITING -> CALLED -> IN_SERVICE -> DONE
```

Alternative skip flows:

```text
WAITING -> SKIPPED
CALLED -> SKIPPED
```

Invalid transitions:

```text
DONE -> WAITING
DONE -> CALLED
SKIPPED -> IN_SERVICE
SKIPPED -> DONE
```

Business rules:

- Check-in creates one queue item for one appointment.
- Queue number must be unique per queue date.
- `DONE` and `SKIPPED` queue items should not appear in the active queue.
- Doctor can only call patients assigned to that doctor.

## 3. VisitStatus

Allowed statuses:

```text
WAITING
IN_CONSULTATION
COMPLETED
CANCELLED
```

Main flow:

```text
WAITING -> IN_CONSULTATION -> COMPLETED
```

Alternative flow:

```text
WAITING -> CANCELLED
```

Business rules:

- A visit represents the actual clinical visit.
- A visit is created when consultation starts or when the workflow needs a persistent visit record after queue check-in.
- One appointment can have at most one visit.
- One queue item can have at most one visit.

## 4. EncounterStatus

Allowed statuses:

```text
OPEN
COMPLETED
```

Main flow:

```text
OPEN -> COMPLETED
```

Invalid transitions:

```text
COMPLETED -> OPEN
```

Business rules:

- `REOPENED` is excluded from MVP.
- One visit can have at most one encounter.
- A completed encounter is read-only in MVP.
- Only the assigned doctor can complete the encounter.
- Invoice generation requires a completed encounter.

## 5. EncounterServiceStatus

Allowed statuses:

```text
ORDERED
COMPLETED
CANCELLED
```

Main flow:

```text
ORDERED -> COMPLETED
ORDERED -> CANCELLED
```

MVP rule:

- Service completion can be simplified. For demo, ordered services may be treated as billable unless cancelled.

## 6. InvoiceStatus

Allowed statuses:

```text
ISSUED
PAID
CANCELLED
```

Main flow:

```text
ISSUED -> PAID
```

Alternative flow:

```text
ISSUED -> CANCELLED
```

Invalid transitions:

```text
PAID -> CANCELLED
PAID -> ISSUED
CANCELLED -> PAID
```

Business rules:

- `DRAFT` is excluded from MVP.
- Generate invoice as `ISSUED` immediately.
- One visit can have at most one invoice.
- Invoice can only be generated after encounter completion.
- A paid invoice cannot be modified.

## 7. PaymentStatus

Allowed statuses:

```text
SUCCESS
FAILED
```

Main flow:

```text
Create payment -> SUCCESS -> Invoice becomes PAID
```

Failure flow:

```text
Create payment -> FAILED -> Invoice remains ISSUED
```

Business rules:

- `REFUNDED` is excluded from MVP.
- Only `ISSUED` invoices can be paid.
- Payment amount must equal invoice total in MVP.
- One invoice has one successful full payment in MVP.
- Failed payments do not change invoice status.

## 8. MVP Status Decisions

Excluded from MVP:

```text
EncounterStatus.REOPENED
InvoiceStatus.DRAFT
PaymentStatus.REFUNDED
```

Reason:

- These statuses create additional permissions, edit rules, rollback rules, and UI states.
- The one-week MVP should focus on a stable end-to-end workflow first.
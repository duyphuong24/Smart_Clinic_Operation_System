# Security Matrix - Smart Clinic Operations System

This document is the MVP role-based access control source of truth for Web Thymeleaf pages and REST APIs.

## 1. Authentication Model

- Web Thymeleaf uses Spring Security form login and server session.
- REST APIs under `/api/v1/**` are reserved for JavaFX and should use stateless JWT when JWT is implemented.
- Login identity is `userName`, not email.
- Database roles are stored without prefix: `ADMIN`, `RECEPTIONIST`, `DOCTOR`, `CASHIER`, `MANAGER`.
- Spring Security authorities must use prefix: `ROLE_ADMIN`, `ROLE_RECEPTIONIST`, `ROLE_DOCTOR`, `ROLE_CASHIER`, `ROLE_MANAGER`.

## 2. Public Access

| Area | URL / Endpoint | Access |
| --- | --- | --- |
| Web login | `/login`, `/process-login` | Public |
| Web static assets | `/css/**`, `/js/**`, `/images/**` | Public |
| API health | `GET /api/v1/health` | Public |
| API login | `POST /api/v1/auth/login` | Public |

All other Web pages and REST APIs require authentication.

## 3. Role Summary

| Role | Main responsibility |
| --- | --- |
| `ADMIN` | Full system administration and master data management |
| `RECEPTIONIST` | Patient registration, appointment booking, check-in, queue coordination |
| `DOCTOR` | Doctor queue, consultation, encounter, service orders |
| `CASHIER` | Invoice and payment workflow |
| `MANAGER` | Dashboard and reports |

## 4. Web URL Matrix

| Web area | Suggested URL pattern | ADMIN | RECEPTIONIST | DOCTOR | CASHIER | MANAGER |
| --- | --- | --- | --- | --- | --- | --- |
| Dashboard | `/dashboard/**` | Yes | No | No | No | Yes |
| Users and roles | `/users/**`, `/roles/**` | Yes | No | No | No | No |
| Staff and doctors | `/staff/**`, `/doctors/**` | Yes | View doctors only | No | No | No |
| Specialty and room | `/specialties/**`, `/rooms/**` | Yes | View only | No | No | No |
| Patients | `/patients/**` | Yes | Yes | View only | No | No |
| Doctor availability | `/doctor-availabilities/**` | Yes | No | No | No | No |
| Appointments | `/appointments/**` | Yes | Yes | View own related appointments | No | No |
| Queue board | `/queue/**`, `/queue-items/**` | Yes | Yes | Own queue only | No | No |
| Visits and encounters | `/visits/**`, `/encounters/**` | Yes | No | Own patients only | No | No |
| Services catalog | `/services/**` | Yes | No | View only | View only | View only |
| Billing and invoices | `/invoices/**` | Yes | No | No | Yes | View only |
| Payments | `/payments/**` | Yes | No | No | Yes | View only |
| Reports | `/reports/**` | Yes | No | No | No | Yes |

## 5. REST API Matrix

| API | ADMIN | RECEPTIONIST | DOCTOR | CASHIER | MANAGER |
| --- | --- | --- | --- | --- | --- |
| `GET /api/v1/auth/me` | Yes | Yes | Yes | Yes | Yes |
| `/api/v1/users/**` | Yes | No | No | No | No |
| `/api/v1/staff/**` | Yes | No | No | No | No |
| `/api/v1/doctors/**` | Yes | Read | Read | No | Read |
| `/api/v1/specialties/**` | Yes | Read | Read | Read | Read |
| `/api/v1/rooms/**` | Yes | Read | Read | Read | Read |
| `/api/v1/patients/**` | Yes | Yes | Read | No | No |
| `/api/v1/doctor-availabilities/**` | Yes | Read | Read | No | No |
| `/api/v1/appointments/**` | Yes | Yes | Read own related | No | No |
| `/api/v1/queue-items/**` | Yes | Yes | Own queue actions | No | No |
| `/api/v1/visits/**` | Yes | No | Own patients only | No | No |
| `/api/v1/encounters/**` | Yes | No | Own patients only | No | No |
| `/api/v1/services/**` | Yes | No | Read | Read | Read |
| `/api/v1/invoices/**` | Yes | No | No | Yes | Read |
| `/api/v1/payments/**` | Yes | No | No | Yes | Read |
| `/api/v1/reports/**` | Yes | No | No | No | Yes |

## 6. JWT Requirement For JavaFX APIs

After JWT is implemented, every protected JavaFX request must include:

```http
Authorization: Bearer <access_token>
```

Expected failure behavior:

| Case | HTTP status | Meaning |
| --- | --- | --- |
| Missing token | `401 Unauthorized` | Client has not logged in |
| Invalid or expired token | `401 Unauthorized` | Client must login again |
| Valid token but wrong role | `403 Forbidden` | User is authenticated but not allowed |
| Business rule violation | `422 Unprocessable Entity` | Request is authenticated but invalid for workflow |

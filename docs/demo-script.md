# Smart Clinic Operations System - Final Presentation Demo Script

**Course:** HSF302 - Working with Spring Boot  
**Project:** Smart Clinic Operations System  
**Architecture:** Modular Monolith (Spring Boot Web + JavaFX Desktop Client + SQL Server DB)  

---

## 1. Demo Roles & Pre-Seeded Accounts

| Role | Username | Password | Purpose in Demo |
|------|----------|----------|-----------------|
| **Admin** | `admin` | `admin123` | System setup, Master Data, Audit Logs, Staff Management |
| **Receptionist** | `receptionist` | `receptionist123` | Patient Registration, Appointment Booking, Queue Check-in |
| **Doctor** | `doctor` | `doctor123` | Queue Consultation, Clinical Encounters, Medical Orders |
| **Cashier** | `cashier` | `cashier123` | Billing, Invoice View, Payment Processing |
| **Manager** | `manager` | `manager123` | Operational Reports, Read-only Audit Logs & Financials |

---

## 2. Step-by-Step Presentation Workflow

### Scene 1: System Overview & Authentication
1. **Web Portal Login (`http://localhost:8080/login`)**:
   - Log in as `admin`. Highlight the responsive glassmorphism UI styled with Thymeleaf layout fragments and CSS variables.
   - Intentionally fail login 5 times with wrong password to demonstrate the Security Requirement: **Automatic 30-minute account locking**.
2. **Desktop Application Startup (`clinic-desktop`)**:
   - Launch JavaFX Desktop app. Log in as `receptionist`.
   - Explain JWT Stateless Authentication + DB-backed Refresh Token Rotation (`SecureRandom` SHA-256 hash in DB).

### Scene 2: Patient Registration & Appointment Scheduling (Receptionist Flow)
1. **Register New Patient**:
   - Navigate to **Patients** tab. Search for existing patient or click **New Patient**.
   - Input: Name: `"Tran Van Demo"`, DOB: `"1998-10-10"`, Phone: `"0912999888"`.
   - Click Save. Highlight Bean Validation feedback and auto-generated Patient Code (`PAT-2026-...`).
2. **Book Appointment**:
   - Navigate to **Appointments** tab. Select Date & Doctor.
   - Book a 09:30 AM slot. Show appointment status badge (`SCHEDULED`).

### Scene 3: Patient Check-in & Real-time Queue (Walk-in / Arrival Flow)
1. **Check-in Patient**:
   - On Appointment list, click **Check-in**.
   - System auto-generates a Queue Ticket (`Q-101`) with status `WAITING`.
2. **Queue Board View**:
   - Open **Queue Board** screen on Desktop client.
   - Show live queue table with Progress Indicators updating asynchronously without blocking JavaFX JavaFX UI thread.

### Scene 4: Clinical Consultation & Service Orders (Doctor Flow)
1. **Call Next Patient**:
   - Log in to Web / Desktop as `doctor`.
   - Pick Queue Ticket `Q-101` and update status to `IN_PROGRESS`.
2. **Conduct Encounter**:
   - Open Encounter Form. Input Symptoms, Chief Complaint, Diagnosis (`ICD-10 J00 - Acute Nasopharyngitis`).
   - Add Service Orders (e.g. *"General Examination"*, *"Blood Test"*).
   - Complete & Lock Encounter. Explain Optimistic Locking (`@Version`) preventing concurrent edits.

### Scene 5: Billing & Payment Processing (Cashier Flow)
1. **Generate Invoice**:
   - Log in as `cashier` (or navigate to Billing module).
   - System displays generated Invoice containing consultation fee and ordered service items.
2. **Record Payment**:
   - Select Payment Method: `CASH` or `BANK_TRANSFER`.
   - Process Payment. Invoice status transitions to `PAID`.

### Scene 6: Security, Audit Logs & Reporting (Admin & Manager Flow)
1. **Audit Logs**:
   - Log in as `admin` or `manager`. Access `/api/v1/audit-logs`.
   - Show logged actions (User logins, Patient creations, Encounter completions, Invoice payments).
   - Demonstrate Role-Based Access Control (RBAC): Show that a `receptionist` attempt to access `/api/v1/audit-logs` returns `403 Forbidden`.
2. **Postman Collection Validation**:
   - Open Postman, run `HSF302-SmartClinic` collection via Collection Runner.
   - Demonstrate 21 API requests passing `200 OK`, `400 Bad Request`, `403 Forbidden` assertions.

---

## 3. Conclusion & Key Takeaways
- **Architecture**: Clean Modular Monolith adhering to Layered Architecture + Repository Pattern.
- **Security**: Robust RBAC, JWT Refresh Token Rotation, BCrypt encoding, and Failed Login Attempt Lockouts.
- **Performance & UX**: Non-blocking JavaFX UI (Task/Service), Thymeleaf layout fragments, and module-specific CSS stylesheets.

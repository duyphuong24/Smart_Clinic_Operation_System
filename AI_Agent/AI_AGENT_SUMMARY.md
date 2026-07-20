# SMART CLINIC SYSTEM - AI AGENT CONVERSATION SUMMARY & ARCHITECTURAL KNOWLEDGE

**Project:** Smart Clinic Operation System (`course-project-hsf302_se20a11_pbt`)  
**Active Git Branch:** `refactor/clean-billing-architecture`  
**Database:** Microsoft SQL Server (`HSF302_SmartClinic` on Host `PHUONGDEV`, DB User `sa`)  
**Test Suite Status:** **Backend 71/71 PASS (100%)** | **Desktop 13/13 PASS (100%)**

---

## 1. DUAL-CLIENT ARCHITECTURE OVERVIEW

1. **Backend Service (`clinic-backend`):**
   - **Framework:** Spring Boot 3.3.2 (Java 21), Spring Data JPA, Spring Security (JWT + Form Login), Thymeleaf Web Engine.
   - **Database Connection:** Connected to MS SQL Server using `.env` file at project root & `clinic-backend/.env`.
   - **REST API Base:** `/api/v1/*` (Stateless JWT Authentication for Desktop Client & external integrations).
   - **Web Admin/Management Base:** `/dashboard`, `/consultation`, `/billing`, `/reports`, `/users`, `/staff` (Session-based Form Login).

2. **Desktop Application (`clinic-desktop`):**
   - **Framework:** JavaFX 21.0.6 (OpenJFX), Jackson Databind.
   - **Architecture:** Layered JavaFX Desktop client communicating with `clinic-backend` via REST API using JWT Token (`TokenStore`, `ApiClient`).
   - **Main Launcher Class:** `com.smartclinic.desktop.app.Launcher` (Calls `Application.launch(ClinicDesktopApplication.class, args)` to ensure clean module initialization).

---

## 2. REFACTORING & KEY TECHNICAL IMPROVEMENTS

### A. Billing Architecture Clean-up (Layered Architecture)
- **Problem:** PR `#60` (`feature/billing-administration`) merged code with `@Autowired` on `EncounterRepository` and `InvoiceRepository` directly inside `BillingController.java`, violating 3-Tier Layered Architecture.
- **Solution:** Encapsulated domain logic in `InvoiceService` & `InvoiceServiceImpl` by adding `findPendingBillings()`. Cleaned `BillingController` to interact exclusively with `InvoiceService`.

### B. HR Record vs System Security Differentiation (`StaffType`)
- **Problem:** Both `admin` and `manager1` had `StaffType.MANAGER`, creating ambiguity between IT System Admin and Clinic Business Manager.
- **Solution:** Added `ADMINISTRATOR` to `StaffType` enum. Updated `DemoDataInitializer.java` and `docs/seed_data.sql`:
  - User `admin` $\rightarrow$ Role `ADMIN` $\rightarrow$ StaffType `ADMINISTRATOR`
  - User `manager1` $\rightarrow$ Role `MANAGER` $\rightarrow$ StaffType `MANAGER`

### C. MS SQL Server Unicode (Vietnamese `NVARCHAR`) & JDBC Driver Fixes
- **Problem 1 (SQL Errors):** `docs/seed_data.sql` had invalid column names (`created_at`/`updated_at` on `roles`, `active` on `users` & `staff`).
- **Problem 2 (Vietnamese `????`):** SQL Server `VARCHAR` strips Vietnamese accents. Fixed script with T-SQL `N'...'` Unicode literal prefix.
- **Problem 3 (JDBC Driver Exception):** Global `use_nationalized_character_data=true` caused MS SQL Server JDBC Driver to fail with `The conversion from varchar to NCHAR is unsupported`.
- **Solution:** Removed global property; explicitly specified `columnDefinition = "nvarchar(150)"` (or `nvarchar(100)`) on JPA entities (`User`, `Patient`, `Room`, `Specialty`, `ServiceCatalog`).

### D. File Cleanup
- Removed over 90 redundant `.gitkeep` placeholder files from populated directories across backend and desktop submodules.

---

## 3. ACTORS & END-TO-END WORKFLOW MAPPING

| Actor | Role / StaffType | Primary Interface | End-to-End Workflow |
| :--- | :--- | :--- | :--- |
| **🛡️ Admin** | `ROLE_ADMIN`<br>`StaffType.ADMINISTRATOR` | Web Admin | System configuration, user & staff account provisioning, master data management (`Room`, `Specialty`, `ServiceCatalog`), security audit log monitoring. |
| **📋 Lễ tân** | `ROLE_RECEPTIONIST`<br>`StaffType.RECEPTIONIST` | JavaFX Desktop & Web | Patient search/creation, appointment scheduling with doctor availability, patient check-in & queue number issuing. |
| **🩺 Bác sĩ** | `ROLE_DOCTOR`<br>`StaffType.DOCTOR` | JavaFX Desktop & Web (`/consultation`) | Room queue management, calling/starting patient consultation, recording medical encounters (chief complaint, diagnosis), ordering lab/procedure services. |
| **💰 Thu ngân** | `ROLE_CASHIER`<br>`StaffType.CASHIER` | JavaFX Desktop & Web (`/billing`) | Reviewing pending billing encounters, invoice generation (`Invoice`), payment processing (`Payment` via Cash/Transfer), receipt printing. |
| **📊 Quản lý** | `ROLE_MANAGER`<br>`StaffType.MANAGER` | Web Admin (`/reports`) | Revenue analytics reports, clinical volume statistics, staff performance monitoring, operational audit trail review. |

---

## 4. COMMAND CHEAT SHEET (RUNNING & VERIFYING)

### A. Run Backend (`clinic-backend`)
```powershell
cd clinic-backend
.\mvnw.cmd spring-boot:run
```

### B. Run Frontend Desktop Client (`clinic-desktop`)
```powershell
cd clinic-desktop
.\mvnw.cmd compile exec:java -Dexec.mainClass="com.smartclinic.desktop.app.Launcher"
```
*Alternative via JavaFX Plugin:*
```powershell
cd clinic-desktop
.\mvnw.cmd clean compile javafx:run
```

### C. Run Fake Data Seed Script on SQL Server
Execute script: [`docs/seed_data.sql`](file:///d:/download%20chrome/Semester_5/HSF302/Assignments/CourseProject/course-project-hsf302_se20a11_pbt/docs/seed_data.sql) in SSMS or IntelliJ Database Tool. Default password for all seed accounts (`admin`, `receptionist1`, `doctor1`, `doctor2`, `cashier1`, `manager1`) is `password123`.

### D. Run Automated Test Suites
```powershell
# Backend tests (71 tests)
cd clinic-backend
.\mvnw.cmd test

# Desktop tests (13 tests)
cd clinic-desktop
.\mvnw.cmd test
```

---

## 5. GENERATED PROJECT DOCUMENTATION ARTIFACTS

- **Academic Report (Word):** [`docs/BAO_CAO_DU_AN_HSF302_SmartClinic.docx`](file:///d:/download%20chrome/Semester_5/HSF302/Assignments/CourseProject/course-project-hsf302_se20a11_pbt/docs/BAO_CAO_DU_AN_HSF302_SmartClinic.docx)
- **Academic Report (Markdown):** [`docs/BAO_CAO_DU_AN_HSF302_SmartClinic.md`](file:///d:/download%20chrome/Semester_5/HSF302/Assignments/CourseProject/course-project-hsf302_se20a11_pbt/docs/BAO_CAO_DU_AN_HSF302_SmartClinic.md)
- **SQL Seed Script:** [`docs/seed_data.sql`](file:///d:/download%20chrome/Semester_5/HSF302/Assignments/CourseProject/course-project-hsf302_se20a11_pbt/docs/seed_data.sql)
- **Git Commit Log:** Clean history pushed to remote `origin/refactor/clean-billing-architecture`.

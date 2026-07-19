# JavaFX Desktop Application Test Plan & Execution Report

**Project:** Smart Clinic Operations System (JavaFX Client)  
**Module:** `clinic-desktop`  
**Requirement Reference:** Section 9.2 (JavaFX Desktop Testing)  
**Execution Date:** 2026-07-20  
**Tested By:** Team QA / Fullstack Engineer  

---

## 1. Overview & Objectives

The purpose of this Test Plan is to verify the functional correctness, UI/UX responsiveness, validation highlighting, REST API integration, token handling, and error resiliency of the JavaFX Desktop client interface.

---

## 2. Test Execution Summary Matrix

| Total Test Cases | Passed | Failed | Blocked | Pass Rate |
|------------------|--------|--------|---------|-----------|
| 12               | 12     | 0      | 0       | **100%**  |

---

## 3. Test Cases & Results

| TC ID | Test Description | Input Data | Expected Result | Actual Result | Status |
|-------|------------------|------------|-----------------|---------------|--------|
| **TC-FX-01** | User Login with valid Admin credentials | Username: `admin`<br>Password: `admin123` | Login successful, JWT access token & refresh token stored in memory, navigation to Main Shell dashboard. | Dashboard loaded successfully with Admin profile header. | **PASS** |
| **TC-FX-02** | User Login with invalid password | Username: `admin`<br>Password: `wrongpass` | System shows Alert error dialog: *"Invalid username or password"*. UI remains on Login view. | Error dialog displayed, password field cleared. | **PASS** |
| **TC-FX-03** | UI Validation - Blank Login Fields | Username: ` ` (empty)<br>Password: ` ` | Submit button disabled or input fields highlighted with `.error` border style. | Input fields highlighted with red `.error` CSS class. | **PASS** |
| **TC-FX-04** | Patient Search - Keyword Filtering | Search Query: `"Nguyen"` | TableView updates asynchronously displaying matching patient records. | Matching patients displayed in TableView without UI freeze. | **PASS** |
| **TC-FX-05** | New Patient Registration | Full Name: `"Tran Van FX"`<br>Phone: `"0987654321"`<br>DOB: `"1995-08-20"` | Patient created via REST API `POST /api/v1/patients`, success Alert displayed, list refreshed. | Success alert displayed, patient code generated automatically. | **PASS** |
| **TC-FX-06** | Today's Appointments Listing | Selection: Today's date | TableView populated with appointments scheduled for the selected date. | TableView populated correctly with status badges (`SCHEDULED`, `CHECKED_IN`). | **PASS** |
| **TC-FX-07** | Walk-in Patient Check-in | Patient: Selected<br>Doctor: Selected | Patient checked in via `POST /api/v1/appointments/check-in`, added to queue, queue number assigned. | Queue item generated, progress spinner hidden upon completion. | **PASS** |
| **TC-FX-08** | Queue Board Monitoring | View: Queue Board screen | Active queue list displayed with status columns (Waiting, In Progress). | Live queue board loaded smoothly with status filters. | **PASS** |
| **TC-FX-09** | Token Refresh Interception (401 Retry) | Action after 15-min token expiration | `ApiClient` intercepts HTTP 401, invokes `/api/v1/auth/refresh`, updates token, and retries request transparently. | Request succeeded seamlessly without logging out user. | **PASS** |
| **TC-FX-10** | Billing & Invoice View | Action: Select Invoice #1 | Invoice details, line items, and total amount rendered in invoice detail view. | Invoice details rendered with accurate calculations. | **PASS** |
| **TC-FX-11** | Role Access Restriction | User: Doctor role | Admin settings menu options hidden or disabled for non-admin roles. | Master Data and Staff management tabs hidden for Doctor login. | **PASS** |
| **TC-FX-12** | User Logout | Action: Click Logout icon | Active session cleared, refresh token revoked via `POST /api/v1/auth/logout`, view navigated back to Login screen. | Session cleared, redirected to Login screen. | **PASS** |

---

## 4. Environment & Test Tools Used

- **Framework:** JavaFX 21 + Scene Builder (FXML)
- **HTTP Client:** Custom `ApiClient` (java.net.http.HttpClient) with 401 Interception
- **Backend API:** `http://localhost:8080` (Spring Boot 3.3.2)
- **Unit Testing Framework:** JUnit 5 + Mockito (`clinic-desktop/src/test/java/...`)

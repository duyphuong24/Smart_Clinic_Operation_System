# Báo cáo Tổng quan Hiện trạng Backend (`backend.md`)
**Hệ thống Smart Clinic Operations System (Backend Core)**  
*Thời gian cập nhật: 2026-07-22*

---

## 1. 📌 Tổng quan Kiến trúc (Backend Architecture Overview)

Phân hệ Backend được thiết kế theo kiến trúc **Domain-Driven Modular Monolith** với Spring Boot 3.3.2 và Java 21. Hệ thống đóng vai trò làm trung tâm xử lý dữ liệu và logic nghiệp vụ cho cả 2 presentation layer: **Web Portal (Thymeleaf)** và **Desktop App (JavaFX Client)**.

### 🏢 Mô hình phân tầng (Layered Architecture):
```text
Thymeleaf MVC Controller (Web Admin)           JavaFX Desktop Client / External REST
               │                                                 │
               ▼                                                 ▼
      Thymeleaf View Engine                          REST API Controllers (/api/v1/*)
               │                                                 │
               └───────────────────────┬─────────────────────────┘
                                       ▼
                       Service Layer & Business Rules
                                       │
                                       ▼
                      Data Access Layer (Spring Data JPA)
                                       │
                                       ▼
                       Database (SQL Server / H2 Test)
```

---

## 2. 🛠 Công nghệ & Thư viện sử dụng (Tech Stack)

| Thành phần | Công nghệ / Thư viện | Phiên bản | Ghi chú |
| --- | --- | --- | --- |
| **Language** | Java | 21 LTS | Sử dụng Virtual Threads, Pattern Matching, Text Blocks |
| **Framework** | Spring Boot | 3.3.2 | Starter Web, Data JPA, Security, Validation, Mail |
| **Security** | Spring Security | 6.x | BCrypt, JWT (`jjwt 0.12.6`), Session-Cookie |
| **Database** | SQL Server / H2 | 2019+ | Dialect SQLServerDialect, H2 cho Unit Test |
| **ORM** | Hibernate / JPA | 6.x | Entity auditing, Optimistic Locking (`@Version`) |
| **Template Engine** | Thymeleaf | 3.1 | Render Web Admin UI và HTML Email Templates |
| **Async & Task** | Spring Task Execution | Spring 6 | `@Async("emailTaskExecutor")`, `@Scheduled` Cronjob |
| **Testing** | JUnit 5 & Mockito | 5.x / 5.x | MockMvc, JaCoCo Coverage Plugin (88/88 Passed) |

---

## 3. 🔒 Kiến trúc Bảo mật & Phân quyền (Security & Authentication)

### 🔹 1. Mô hình Xác thực Kép (Dual Authentication Model)
- **Web Admin Portal**: Sử dụng **Stateful Session-Cookie Authentication** với Spring Security mặc định.
- **JavaFX Desktop & External APIs**: Sử dụng **Stateless JWT Authentication** (`Bearer <token>`).
  - Access Token hết hạn sau 1 giờ (`3600s`).
  - Refresh Token hết hạn sau 7 ngày (`604800s`), lưu băm SHA-256 trong bảng `refresh_tokens`, hỗ trợ cơ chế phát hiện thu hồi token nghi vấn (Revocation Detection).

### 🔹 2. Cơ chế Khóa tài khoản (Account Lockout Protection)
- Khi đăng nhập sai quá 5 lần (`failedLoginAttempts >= 5`), tài khoản tự động bị khóa (`UserStatus.LOCKED`) và ghi nhận `lockTime`.
- Sử dụng `@Transactional(noRollbackFor = BadCredentialsException.class)` tại `AuthService.login` để đảm bảo cờ đếm đếm số lần thử luôn được lưu vào DB ngay cả khi Spring Security ném lỗi xác thực.

### 🔹 3. Phân quyền vai trò (Role-Based Access Control - RBAC)
- **Danh sách Role**: `ROLE_ADMIN`, `ROLE_DOCTOR`, `ROLE_RECEPTIONIST`, `ROLE_CASHIER`, `ROLE_MANAGER`, `ROLE_PATIENT`.
- Tất cả REST Endpoints đều được bảo vệ bởi `@PreAuthorize("hasRole(...)")` và matcher tại `SecurityConfig`.

---

## 4. 🗂 Cấu trúc Package & Phân hệ Chức năng (`com.smartclinic`)

Dự án tổ chức theo **Domain-Based Packages**:

```text
com.smartclinic
 ├── auth            # API Đăng nhập, Refresh Token, Logout, Current User
 ├── security        # SecurityConfig, JwtService, CustomUserDetailsService, Handlers
 ├── user            # UserRestController, UserService, Role, User Entity & DTOs
 ├── staff           # Quản lý hồ sơ nhân viên (Receptionist, Cashier, Doctor, Manager)
 ├── doctor          # Quản lý thông tin bác sĩ, chứng chỉ, phòng mặc định, giá khám
 ├── masterdata      # RoomRestController, SpecialtyRestController (Phòng & Chuyên khoa)
 ├── patient         # PatientRestController, PatientService (Hồ sơ & Lịch sử khám)
 ├── schedule        # DoctorAvailabilityRestController (Lịch làm việc bác sĩ)
 ├── appointment     # AppointmentRestController (Đặt lịch, Đổi lịch, Hủy, No-show)
 ├── queue           # QueueItemRestController (Xếp hàng, Gọi tên, Chuyển phòng, Re-queue)
 ├── visit           # Quản lý Lượt khám bệnh (Visit)
 ├── encounter       # EncounterRestController (Khám lâm sàng, Sinh hiệu, Chẩn đoán)
 ├── servicecatalog  # ServiceCatalogRestController (Danh mục Dịch vụ y tế)
 ├── invoice         # InvoiceRestController (Tạo hóa đơn dịch vụ khám)
 ├── payment         # PaymentRestController (Ghi nhận thanh toán Tiền mặt/Thẻ/Chuyển khoản)
 ├── report          # ReportRestController (Báo cáo doanh thu & Thống kê lượt khám)
 ├── audit           # AuditLogRestController (Nhật ký truy cập & Thao tác hệ thống)
 ├── notification    # EmailService, NotificationService, AppointmentReminderScheduler
 ├── common          # ApiResponse, PageResponse, GlobalExceptionHandler, BaseEntity
 └── config          # AsyncAndSchedulingConfig (ThreadPool, Scheduled enable)
```

---

## 5. ⚡ Tính năng Nổi bật mới Triển khai (Featured Core Capabilities)

### 🚀 1. User Management (`UserRestController`)
- **API `/api/v1/users`**: Tìm kiếm phân trang, Tạo mới tài khoản (BCrypt), Cập nhật thông tin, Khóa/Mở khóa tài khoản, Đặt lại mật khẩu (`reset-password`).

### 🚀 2. Bổ sung các REST APIs nghiệp vụ nâng cao
- **Appointment**: API `PATCH /api/v1/appointments/{id}/no-show` (đánh dấu bệnh nhân không đến).
- **Patient**: API `DELETE /api/v1/patients/{id}` (vô hiệu hóa hồ sơ) & `GET /api/v1/patients/{id}/medical-history` (lịch sử khám bệnh).
- **Queue Management**: API `PATCH /api/v1/queue-items/{id}/transfer` (điều chuyển hàng đợi sang Bác sĩ/Phòng khác, tự động sinh số thứ tự mới) & `POST /api/v1/queue-items/{id}/re-queue` (cho phép bệnh nhân trễ lượt `SKIPPED` xếp hàng lại).

### 🚀 3. Engine Thông báo Bất đồng bộ & Gửi Mail Nhắc lịch (`com.smartclinic.notification`)
- **Gửi Email Bất đồng bộ (`@Async("emailTaskExecutor")`)**:
  - Tự động gửi Email xác nhận ngay khi đặt lịch hẹn thành công (`BOOKED`).
  - Render giao diện Email HTML chuyên nghiệp bằng Thymeleaf (`appointment-confirmation.html`, `appointment-reminder-1day.html`).
- **Lập lịch Cronjob Tự động (`@Scheduled(cron = "0 0 8 * * ?")`)**:
  - Chạy 8h sáng hàng ngày quét các lịch hẹn `BOOKED` vào ngày mai (`LocalDate.now().plusDays(1)`) chưa gửi nhắc nhở (`reminderSent = false`) để tự động gửi mail nhắc bệnh nhân.
- **Fallback Console Mode**:
  - Hỗ trợ cờ `smartclinic.notification.email-enabled=false`. Khi chưa cấu hình SMTP, hệ thống tự động ghi log thông báo ra console mà không làm gián đoạn ứng dụng.

---

## 6. 🧪 Kết quả Kiểm thử Tự động (Automated Test Status)

- **Công cụ kiểm thử**: JUnit 5, Mockito, Spring Security Test, MockMvc, JaCoCo Plugin.
- **Lệnh chạy kiểm thử**: `mvnw.cmd test`
- **Trạng thái kiểm thử**:
  ```text
  [INFO] Results:
  [INFO] 
  [INFO] Tests run: 88, Failures: 0, Errors: 0, Skipped: 0
  [INFO] 
  [INFO] ------------------------------------------------------------------------
  [INFO] BUILD SUCCESS
  [INFO] ------------------------------------------------------------------------
  ```
- **Tỷ lệ vượt qua (Pass Rate)**: **100% (88/88 Test Cases PASSED)**.
- **Các bộ Test chính**:
  1. `AuthIntegrationTest`: Kiểm tra xác nhận đăng nhập, mã hóa token, đếm 5 lần khóa tài khoản.
  2. `UserServiceImplTest`: Kiểm tra CRUD user, trùng username, đổi mật khẩu.
  3. `PatientServiceImplTest`: Kiểm tra tạo mã bệnh nhân, vô hiệu hóa, lịch sử khám.
  4. `AppointmentServiceImplTest`: Kiểm tra trùng trùng lịch khám bác sĩ, hủy hẹn, no-show.
  5. `QueueItemServiceImplTest`: Kiểm tra gọi tên, bỏ lượt, chuyển phòng, re-queue.
  6. `InvoiceAndPaymentIntegrationTest`: Kiểm tra tính tiền hóa đơn và ghi nhận thanh toán.
  7. `EmailServiceImplTest`, `NotificationServiceImplTest`, `AppointmentReminderSchedulerTest`: Kiểm tra gửi mail HTML ngầm và Cronjob nhắc lịch ngày mai.
  8. `WorkflowBusinessRulesTest` & `SecurityAuthorizationTest`: Kiểm tra luồng liên vết nghiệp vụ và phân quyền API.

---

## 7. 🔮 Định hướng phát triển tiếp theo (Next Roadmap)

1. Tích hợp thanh toán trực tuyến qua VNPAY / PayOS Webhook cho hóa đơn viện phí.
2. Nâng cấp Elasticsearch / Full-Text Search cho tính năng tìm kiếm bệnh nhân nhanh mờ.
3. Đóng gói Container Docker Compose cho Spring Boot + SQL Server + MailHog.

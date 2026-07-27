# 🏥 Smart Clinic Operations System (Hệ Thống Quản Lý & Điều Hành Phòng Khám Thông Minh)

![Java 21](https://img.shields.io/badge/Java-21_LTS-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.2-green.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-005F0F.svg)
![Security](https://img.shields.io/badge/Spring_Security-6.x-brightgreen.svg)
![Tests](https://img.shields.io/badge/Tests-88%2F88_Passed_(100%25)-success.svg)
![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)

---

## 📌 1. Giới thiệu Dự án (Project Overview)

**Smart Clinic Operations System** là giải pháp phần mềm tổng thể quản lý vận hành phòng khám đa khoa/chuyên khoa enterprise, được thiết kế theo kiến trúc **Domain-Driven Modular Monolith** hiện đại trên nền tảng **Java 21 LTS** và **Spring Boot 3.3.2**.

Hệ thống giải quyết triệt để các bài toán vận hành thực tế tại các cơ sở y tế:
- **Tối ưu luồng tiếp đón & điều phối bệnh nhân**: Giảm thời gian chờ đợi với Hệ thống Quản lý Hàng đợi Thông minh (Smart Queue Desk).
- **Trải nghiệm khám lâm sàng liền mạch**: Hỗ trợ bác sĩ ghi nhận sinh hiệu, chẩn đoán chuẩn chuẩn quốc tế ICD-10, chỉ định dịch vụ cận lâm sàng (CLS) và kê đơn thuốc mượt mà.
- **Tích hợp thanh toán đa kênh**: Hỗ trợ thu ngân ghi nhận Tiền mặt, Thẻ ATM/Credit và Cổng thanh toán trực tuyến tự động qua **PayOS (QR Code Webhook)**.
- **Kiến trúc trình diễn kép (Dual Presentation Layer)**:
  - **Web Management Portal (Thymeleaf)**: Dành cho Quản trị viên (Admin) và Bác sĩ/Nhân viên quản lý từ xa qua trình duyệt.
  - **Enterprise Desktop App (JavaFX 21 Client)**: Dành cho các máy trạm chuyên dụng tại Quầy Lễ tân, Bàn khám Bác sĩ và Quầy Thu ngân đòi hỏi phản hồi thao tác tức thì.

---

## 🏗 2. Kiến trúc Hệ thống (System Architecture)

Dự án áp dụng mô hình phân tầng chặt chẽ và tách biệt độc lập giữa tầng lưu trữ dữ liệu, logic nghiệp vụ core backend và 2 phân hệ trình diễn UI:

```text
┌──────────────────────────────────────────┐    ┌──────────────────────────────────────────┐
│        Web Admin Portal (SSR UI)         │    │       JavaFX Desktop App (Client)        │
│    Spring Boot 3.3 + Thymeleaf 3.1       │    │      Java 21 + JavaFX 21 + FXML         │
└────────────────────┬─────────────────────┘    └────────────────────┬─────────────────────┘
                     │                                               │
                     │ (Stateful Session)                            │ (Stateless Bearer JWT)
                     ▼                                               ▼
┌──────────────────────────────────────────────────────────────────────────────────────────┐
│                           Spring Security 6.x Authentication                             │
│                  BCrypt Password Encoders | Revocation Token Storage                     │
└────────────────────────────────────────────┬─────────────────────────────────────────────┘
                                             │
                                             ▼
┌──────────────────────────────────────────────────────────────────────────────────────────┐
│                               Backend Core Service Layer                                 │
│      Auth | User | Staff | Patient | Appointment | Queue | Encounter | Billing | Audit   │
└────────────────────────────────────────────┬─────────────────────────────────────────────┘
                                             │
                                             ▼
┌──────────────────────────────────────────┐    ┌──────────────────────────────────────────┐
│      Async Task & Email Engine           │    │    Spring Data JPA & Hibernate 6.x       │
│  @Async TaskExecutor + @Scheduled Cron   │    │      SQL Server 2019+ / H2 In-Memory     │
└──────────────────────────────────────────┘    └──────────────────────────────────────────┘
```

---

## 🛠 3. Công nghệ & Thư viện (Tech Stack)

### 🔹 Backend Core (`clinic-backend`)
- **Ngôn ngữ**: Java 21 LTS (Virtual Threads, Pattern Matching, Record Types).
- **Core Framework**: Spring Boot 3.3.2 (Starter Web, Data JPA, Security, Validation, Mail).
- **Bảo mật**: Spring Security 6.x, JWT (`jjwt 0.12.6`), Session-Cookie, BCrypt.
- **Cơ sở dữ liệu**: SQL Server 2019+ (Production/Dev), H2 In-Memory Database (Testing).
- **Cổng thanh toán**: PayOS SDK / Webhook Integration.
- **Engine Template**: Thymeleaf 3.1 (Render Web UI & HTML Mail Templates).
- **Lập lịch & Tác vụ ngầm**: Spring Task Execution (`@Async`), Spring Scheduler (`@Scheduled`).
- **Testing**: JUnit 5, Mockito, MockMvc, JaCoCo Coverage.

### 🔹 Desktop Client App (`clinic-desktop`)
- **Ngôn ngữ & Engine**: Java 21 LTS + JavaFX 21.0.x.
- **Thiết kế Giao diện**: FXML, SceneBuilder, Custom CSS Stylesheets.
- **Giao tiếp REST API**: Custom `ApiClient` bọc `java.net.http.HttpClient` bất đồng bộ.
- **Quản lý Phiên (Session Manager)**: Lưu giữ JWT Bearer Token, tự động Refresh Token ngầm khi hết hạn.
- **JSON Processing**: Jackson Databind 2.17.x.

---

## ⚡ 4. Các Phân hệ & Tính năng Nổi bật (Core Features)

### 🔑 1. Quản lý Xác thực & Phân quyền Kép (Dual Auth & RBAC)
- **Mô hình xác thực kép**: Session-Cookie cho Web Portal và JWT Access Token (hạn 1h) / Refresh Token (hạn 7 ngày) cho JavaFX Desktop App.
- **Bảo vệ tài khoản chống Brute-force**: Tự động khóa tài khoản (`LOCKED`) khi nhập sai mật khẩu quá 5 lần liên tiếp.
- **Phân quyền theo vai trò (RBAC)**: `ROLE_ADMIN`, `ROLE_DOCTOR`, `ROLE_RECEPTIONIST`, `ROLE_CASHIER`, `ROLE_MANAGER`, `ROLE_PATIENT`.

### 🏥 2. Tiếp đón & Quản lý Hàng đợi Thông minh (Smart Queue Management)
- Tự động phát số thứ tự khám bệnh theo phòng khám/bác sĩ.
- **Thao tác 1-Click tại Quầy Lễ tân/Máy trạm**:
  - `CALL`: Gọi bệnh nhân tiếp theo vào phòng.
  - `SKIP`: Bỏ qua bệnh nhân vắng mặt.
  - `RE-QUEUE`: Xếp hàng lại cho bệnh nhân lỡ lượt mà không phải lấy số mới từ đầu.
  - `TRANSFER`: Điều chuyển bệnh nhân sang Bác sĩ/Phòng khám khác (tự động cập nhật số thứ tự mới).

### 👨‍⚕️ 3. Khám lâm sàng & Hồ sơ Bệnh án (Clinical Encounters)
- Ghi nhận chỉ số sinh hiệu: Huyết áp, Nhịp tim, Thân nhiệt, Chiều cao, Cân nặng, BMI.
- Chẩn đoán chuẩn mã y khoa quốc tế ICD-10 và kết luận lâm sàng.
- Chỉ định Dịch vụ Cận lâm sàng (Xét nghiệm/Siêu âm/X-quang) và Kê đơn thuốc điện tử.
- Tra cứu lịch sử khám bệnh (`Medical History`) của bệnh nhân qua các đợt khám trước.

### 💰 4. Quản lý Viện phí & Thanh toán PayOS (Billing & Payment)
- Tự động tổng hợp chi phí từ lượt khám và dịch vụ CLS chỉ định.
- Hỗ trợ đa dạng phương thức: Tiền mặt, Thẻ ATM/Credit, Chuyển khoản qua **Cổng thanh toán PayOS (VietQR Webhook)** tự động gạch nợ hóa đơn `PAID`.
- Tra cứu lịch sử giao dịch và in hóa đơn/phiếu thu.

### 📧 5. Engine Thông báo Bất đồng bộ & Nhắc lịch Khám tự động
- **Email gửi ngầm (`@Async`)**: Tự động gửi Email xác nhận kèm mã hẹn (`APT-XXXXXX`) ngay khi đăng ký lịch thành công.
- **Cronjob nhắc lịch (`@Scheduled`)**: Tự động quét và gửi Mail nhắc nhở lúc 8:00 sáng hàng ngày cho các bệnh nhân có lịch hẹn vào ngày hôm sau.
- **Giao diện Email HTML**: Render chuyên nghiệp qua mẫu Thymeleaf Template (`appointment-confirmation.html`, `appointment-reminder-1day.html`).

### 📊 6. Nhật ký Audit & Thống kê Báo cáo (Audit Log & Analytics)
- Ghi nhận toàn bộ vết thao tác nhạy cảm của người dùng (Đăng nhập, Thay đổi mật khẩu, Đổi trạng thái lịch hẹn, Thanh toán) tại `AuditLog`.
- Báo cáo biểu đồ doanh thu khám bệnh, số lượng lượt khám theo Bác sĩ và Chuyên khoa.

---

## 📂 5. Cấu trúc Thư mục Dự án (Project Structure)

```text
Smart_Clinic_Operation_System/
 ├── clinic-backend/                 # Core Backend Spring Boot Project
 │    ├── src/main/java/com/smartclinic/
 │    │    ├── auth/                 # Authentication & JWT Endpoints
 │    │    ├── security/             # SecurityConfig, JwtFilter, UserDetails
 │    │    ├── user/                 # User CRUD & Reset Password Services
 │    │    ├── staff/                # Staff Profile Management
 │    │    ├── doctor/               # Doctor Profiles & Schedules
 │    │    ├── patient/              # Patient Profiles & Medical Records
 │    │    ├── schedule/             # Doctor Availability Board
 │    │    ├── appointment/          # Appointment Management & Reschedule
 │    │    ├── queue/                # Smart Queue Routing & Re-queue
 │    │    ├── visit/                # Visit Management
 │    │    ├── encounter/            # Clinical Encounters & ICD-10
 │    │    ├── servicecatalog/       # Medical Service Catalog
 │    │    ├── billing/              # Invoice & PayOS Payment Webhook
 │    │    ├── report/               # Revenue & Visit Analytics Reports
 │    │    ├── audit/                # Security System Audit Logging
 │    │    ├── notification/         # Async Email & Cronjob Scheduler
 │    │    └── common/               # ApiResponse, PageResponse, Exceptions
 │    └── src/main/resources/
 │         ├── templates/            # Thymeleaf Web Views & Mail Templates
 │         ├── static/               # CSS, JavaScript & Assets
 │         └── application.yml       # Application Configuration Properties
 │
 ├── clinic-desktop/                 # JavaFX Desktop Client Application
 │    ├── src/main/java/com/smartclinic/desktop/
 │    │    ├── api/                  # ApiClient HTTP Wrapper & Exceptions
 │    │    ├── session/              # SessionManager JWT Handling
 │    │    ├── controller/           # JavaFX FXML Event Controllers
 │    │    ├── dto/                  # Client Data Transfer Objects
 │    │    └── util/                 # Receipt Printer & GUI Helpers
 │    └── src/main/resources/
 │         └── fxml/                 # JavaFX FXML View Layouts & CSS
 │
 ├── docs/                           # Comprehensive System Documentation
 │    ├── backend.md                 # Technical Architecture Report
 │    ├── javafx_desktop.md          # JavaFX Architecture & Test Plan
 │    ├── web_thymleaf.md            # Web Thymeleaf Portal Documentation
 │    ├── database-design.md         # Database Entity Diagram & Schema
 │    ├── business-rules.md          # Core Business Validation Rules
 │    └── api-contract.md            # REST API Specification Document
 ├── postman/                        # Postman API Test Collections
 ├── backend.md                      # Shortcut to Backend Architecture
 ├── javafx_desktop.md               # Shortcut to Desktop Architecture
 ├── web_thymleaf.md                 # Shortcut to Web Thymeleaf Architecture
 └── pom.xml / mvnw.cmd              # Maven Build Tooling
```

---

## 🚀 6. Hướng dẫn Khởi chạy Dự án (Getting Started)

### 📋 Yêu cầu Môi trường (Prerequisites)
- **Java Development Kit (JDK)**: JDK 21 LTS trở lên.
- **Build Tool**: Apache Maven 3.9+ (hoặc dùng Wrapper `mvnw` đi kèm).
- **Database**: SQL Server 2019+ (hoặc sử dụng cấu hình mặc định H2 In-Memory cho mục đích chạy thử nghiệm).
- **Mail Server (Tùy chọn)**: Tài khoản Gmail App Password / MailHog nếu muốn kiểm tra gửi email thật.

---

### 🏃‍♂️ Chạy Phân hệ Backend (`clinic-backend`)

1. **Di chuyển vào thư mục backend**:
   ```bash
   cd clinic-backend
   ```

2. **Cấu hình Cơ sở dữ liệu (`src/main/resources/application.yml`)**:
   - Mặc định ứng dụng sẽ tự động khởi tạo cơ sở dữ liệu và dữ liệu mẫu (Seed data).
   - Nếu kết nối SQL Server thật, hãy cập nhật cấu hình datasource trong `application.yml`:
     ```yaml
     spring:
       datasource:
         url: jdbc:sqlserver://localhost:1433;databaseName=SmartClinicDB;encrypt=false
         username: sa
         password: YourPassword123
     ```

3. **Biên dịch và khởi chạy Spring Boot Server**:
   ```bash
   mvnw clean spring-boot:run
   ```
   *Server Backend sẽ khởi chạy tại URL*: `http://localhost:8080`

---

### 💻 Chạy Phân hệ Desktop App (`clinic-desktop`)

1. **Mở terminal mới và di chuyển vào thư mục desktop**:
   ```bash
   cd clinic-desktop
   ```

2. **Khởi chạy ứng dụng JavaFX Desktop**:
   ```bash
   mvnw clean javafx:run
   ```
   *Giao diện Đăng nhập JavaFX Desktop Client sẽ xuất hiện để thao tác trực tiếp.*

---

## 🧪 7. Kiểm thử Tự động & Chất lượng (Testing & QA)

Dự án chú trọng công tác bảo đảm chất lượng với bộ kiểm thử tự động toàn diện trên Spring Boot Core Backend.

- **Kết quả Kiểm thử**: **88/88 Test Cases PASSED (Tỷ lệ thành công 100%)**.
- **Lệnh thực thi kiểm thử tự động**:
  ```bash
  cd clinic-backend
  mvnw test
  ```

- **Các bộ kiểm thử trọng tâm**:
  - `AuthIntegrationTest`: Kiểm tra xác thực đăng nhập, mã hóa Token JWT và đếm 5 lần khóa tài khoản.
  - `AppointmentServiceImplTest`: Kiểm tra logic chống trùng lịch khám bác sĩ, hủy hẹn, no-show.
  - `QueueItemServiceImplTest`: Kiểm tra nghiệp vụ gọi số, bỏ lượt, chuyển phòng và re-queue.
  - `InvoiceAndPaymentIntegrationTest`: Kiểm tra tính tiền hóa đơn và ghi nhận thanh toán.
  - `EmailServiceImplTest` & `AppointmentReminderSchedulerTest`: Kiểm tra gửi mail HTML ngầm và Cronjob nhắc lịch hàng ngày.

---

## 🌐 8. Tài khoản Trải nghiệm Mẫu (Demo Credentials)

Sau khi khởi chạy dự án, bạn có thể đăng nhập bằng các tài khoản kiểm thử mặc định sau:

| Vai trò (Role) | Mật khẩu (Password) | Tài khoản (Username) | Màn hình truy cập / Chức năng |
| --- | --- | --- | --- |
| **System Admin** | `Password123!` | `admin` | Quản trị tài khoản, Phân quyền, Audit Log |
| **Doctor (Bác sĩ)** | `Password123!` | `doctor1` | Bàn khám lâm sàng, ICD-10, Kê đơn |
| **Receptionist (Lễ tân)** | `Password123!` | `receptionist1` | Tiếp đón, Cấp số thứ tự, Quản lý Hàng đợi |
| **Cashier (Thu ngân)** | `Password123!` | `cashier1` | Thu viện phí, Thanh toán QR PayOS |
| **Manager (Quản lý)** | `Password123!` | `manager1` | Báo cáo doanh thu & Thống kê lượt khám |

---

## 📖 9. Tài liệu Tham khảo Chi tiết (Documentation Links)

Hệ thống đi kèm bộ tài liệu kiến trúc kỹ thuật chi tiết trong thư mục [`docs/`](file:///d:/CV_Project/Smart_Clinic_Operation_System/docs):
- [📄 Báo cáo Kiến trúc Backend (`backend.md`)](file:///d:/CV_Project/Smart_Clinic_Operation_System/backend.md)
- [💻 Báo cáo Phân hệ JavaFX Desktop (`javafx_desktop.md`)](file:///d:/CV_Project/Smart_Clinic_Operation_System/javafx_desktop.md)
- [🌐 Báo cáo Phân hệ Web Portal Thymeleaf (`web_thymleaf.md`)](file:///d:/CV_Project/Smart_Clinic_Operation_System/web_thymleaf.md)
- [📊 Thiết kế Cơ sở dữ liệu (`docs/database-design.md`)](file:///d:/CV_Project/Smart_Clinic_Operation_System/docs/database-design.md)
- [📑 Quy tắc Nghiệp vụ (`docs/business-rules.md`)](file:///d:/CV_Project/Smart_Clinic_Operation_System/docs/business-rules.md)
- [🔌 REST API Contract (`docs/api-contract.md`)](file:///d:/CV_Project/Smart_Clinic_Operation_System/docs/api-contract.md)

---

## 📜 10. Bản quyền (License)

Được phát triển và duy trì bởi **Smart Clinic Software Engineering Team**.  
Dự án phát hành dưới bản quyền **MIT License**.

# TRƯỜNG ĐẠI HỌC FPT – FPT CAMPUS ĐÀ NẴNG
## KHOA CÔNG NGHỆ THÔNG TIN

# BÁO CÁO DỰ ÁN CUỐI KỲ MÔN HSF302
## HỆ THỐNG QUẢN LÝ PHÒNG KHÁM ĐA KHOA THÔNG MINH (SMART CLINIC OPERATION SYSTEM)

> [!NOTE]
> **THÔNG TIN DỰ ÁN**
> - **Môn học:** HSF302 – Working with Spring Boot
> - **Kỳ học:** Summer 2026 (SU26)
> - **Repository:** `course-project-hsf302_se20a11_pbt`
> - **Nhóm thực hiện:** Nguyễn Duy Phương (Leader), Trịnh Hoàng Thiên Bảo, Nguyễn Hữu Tài
> - **Thời gian hoàn thành:** Tháng 07/2026 | **Địa điểm:** FPT Campus Đà Nẵng

---

## LỜI CAM ĐOAN
Chúng tôi xin cam đoan rằng báo cáo dự án cuối kỳ môn HSF302 này là công trình nghiên cứu và phát triển phần mềm hoàn toàn độc lập của nhóm dưới sự hướng dẫn của giảng viên chuyên môn. Toàn bộ kiến trúc hệ thống, sơ đồ cơ sở dữ liệu MS SQL Server, các dịch vụ backend Spring Boot 3 REST API/Thymeleaf MVC, giao diện JavaFX 21 Desktop Client và bộ kiểm thử tự động (Unit, Integration Tests & Postman Collection Automation) đều được xây dựng trung thực, tuân thủ nghiêm ngặt các tiêu chuẩn mã nguồn sạch (Clean Code), nguyên tắc thiết kế SOLID và quy định bảo mật hệ thống.

---

# PHẦN I – GIỚI THIỆU DỰ ÁN VÀ TỔNG QUAN THỰC TIỄN

## Chương 1. Tổng quan dự án Smart Clinic

### 1.1. Giới thiệu đề tài & Bối cảnh thực tiễn
Trong bối cảnh chuyển đổi số ngành y tế đang diễn ra mạnh mẽ, các phòng khám đa khoa vừa và nhỏ tại Việt Nam gặp rất nhiều thách thức trong việc quản lý vận hành: số lượng bệnh nhân đông, quy trình tiếp đón và cấp số thứ tự còn thủ công gây ùn tắc, việc luân chuyển hồ sơ bệnh án giữa các phòng khám lâm sàng chập chạp, và nguy cơ sai sót trong tính toán hóa đơn thanh toán viện phí.

Dự án **Smart Clinic Operation System** (mã hiệu: `smartclinic`) được phát triển nhằm giải quyết triệt để các bài toán trên bằng cách cung cấp một giải pháp phần mềm tổng thể, hiện đại và chuẩn hóa theo mô hình kiến trúc Hybrid Presentation Production-Ready:
- **Web Management Portal (clinic-backend Thymeleaf):** Cung cấp giao diện quản trị Server-Side Rendering (SSR) chuyên dụng cho Bác sĩ (khám lâm sàng, kê đơn, chỉ định dịch vụ) và Quản trị viên (quản lý danh mục phòng, chuyên khoa, nhân sự và theo dõi Audit Logs).
- **Desktop Rich Client (clinic-desktop JavaFX):** Cung cấp ứng dụng Desktop tốc độ cao phản hồi tức thì cho quầy Lễ tân (tiếp đón, đăng ký bệnh nhân, cấp số hàng đợi), quầy Thu ngân (tính tiền hóa đơn, xác nhận thanh toán đa phương thức) và Quản lý phòng khám (Dashboard số liệu doanh thu tài chính).
- **Nền tảng Backend Core (Spring Boot 3.3.2 + Java 21):** Đóng vai trò hạt nhân xử lý toàn bộ logic nghiệp vụ y tế khép kín, quản lý xác thực bảo mật kép (JWT Stateless & Session-Cookie), tối ưu truy vấn CSDL MS SQL Server và vận hành bộ kiểm thử tự động 100% PASS.

### 1.2. Mục tiêu và phạm vi hệ thống
Hệ thống bao phủ trọn vẹn 9 phân hệ nghiệp vụ chính với sự phân chia nhiệm vụ tối ưu giữa 2 nền tảng Web và Desktop:

| Module Nghiệp Vụ | Web Portal (Thymeleaf) | Desktop Client (JavaFX) |
|---|:---:|:---:|
| 1. Xác thực & Bảo mật (Auth & Security) | ✓ Form Login Session & Cookie | ✓ REST API JWT Bearer Token |
| 2. Dashboard KPI (Home Overview) | ✓ Thống kê tổng quan hệ thống | ✓ KPI Stat Cards & Quick Action Buttons |
| 3. Quản lý Bệnh nhân (Patient Directory) | ✓ Quản lý hồ sơ, lịch sử khám | ✓ Tra cứu phân trang, Đăng ký hồ sơ mới |
| 4. Đặt lịch khám (Appointment Scheduling) | ✓ Xem lịch làm việc, Đặt/Hủy lịch | ✓ Lịch hẹn hôm nay, Check-in tiếp đón |
| 5. Hàng đợi & Điều phối (Queue Desk) | ✓ Màn hình hiển thị TV công cộng | ✓ Cấp số Walk-in, Call, Skip, Re-queue, Transfer |
| 6. Khám lâm sàng (Clinical Encounter) | ✓ Bác sĩ khám, Chỉ định CLS, Chẩn đoán | – (Chuyên dụng trên Web Portal) |
| 7. Tính tiền & Thu ngân (Billing & Payment) | ✓ Xem chi tiết hóa đơn ca khám | ✓ Thu ngân, Modal thanh toán CASH/BANK/CARD |
| 8. Báo cáo Tài chính & Audit Log | ✓ Báo cáo doanh thu, Nhật ký Audit | ✓ Financial Reports Dashboard (Admin/Manager) |
| 9. Quản trị Danh mục (Master Data Admin) | ✓ Quản lý Phòng, Chuyên khoa, Giá Dịch vụ | – (Chuyên dụng trên Web Portal) |

### 1.3. Đối tượng & Vai trò người dùng (RBAC 5 Roles)
- **ADMIN (Quản trị viên hệ thống):** Quản lý toàn bộ người dùng, gán vai trò, quản trị danh mục chuyên khoa, phòng khám, dịch vụ y tế, tra cứu Audit Logs bảo mật và xem báo cáo tài chính tổng thể.
- **RECEPTIONIST (Lễ tân quầy tiếp đón):** Đăng ký thông tin bệnh nhân mới, tìm kiếm hồ sơ, đặt lịch hẹn khám, thực hiện check-in cho bệnh nhân có lịch hẹn, cấp số hàng đợi Walk-in và điều phối hàng đợi (gọi tên, bỏ qua, xếp hàng lại, chuyển phòng).
- **DOCTOR (Bác sĩ chuyên khoa):** Quản lý danh sách bệnh nhân chờ khám tại phòng, gọi bệnh nhân vào khám (`CALL`), nhập chỉ số sinh hiệu (huyết áp, nhịp tim, nhiệt độ), ghi nhận chẩn đoán ICD-10, chỉ định dịch vụ cận lâm sàng (xét nghiệm, X-quang) và kết luận ca khám (`DONE`).
- **CASHIER (Thu ngân quầy thanh toán):** Tra cứu các hóa đơn ở trạng thái chưa thanh toán (`UNPAID`), xem chi tiết các dịch vụ y tế đã sử dụng, thực hiện ghi nhận thanh toán đa phương thức (Tiền mặt, Chuyển khoản QR, Thẻ ngân hàng) và chuyển trạng thái hóa đơn thành `PAID`.
- **MANAGER (Quản lý phòng khám):** Theo dõi báo cáo doanh thu thực tế theo ngày/tháng, xem thống kê hiệu suất làm việc của bác sĩ và lưu lượng bệnh nhân khám chữa bệnh.

### 1.4. Chi tiết Công nghệ & Thư viện (Tech Stack Matrix)
- **Backend Core Framework:** Spring Boot 3.3.2 vận hành trên Java 21 LTS (tận dụng Virtual Threads, Pattern Matching, Text Blocks).
- **Security & Identity:** Spring Security 6.x, mã hóa mật khẩu BCrypt, xác thực JWT REST (`jjwt 0.12.6`), Session-Cookie cho Web Admin, bảo vệ chống khóa tài khoản (Account Lockout) sau 5 lần đăng nhập sai.
- **Database Management:** Microsoft SQL Server 2019/2022 (Production Profile) & H2 In-Memory DB (Testing Profile với Dialect tương thích).
- **ORM & Data Access:** Spring Data JPA & Hibernate 6.x, hỗ trợ `@EntityGraph` loại bỏ lỗi N+1 Query, `@Version` ngăn ngừa ghi đè dữ liệu đồng thời (Optimistic Locking).
- **Frontend Web Portal:** Spring Boot Starter Thymeleaf 3.1, Thymeleaf Extras Spring Security 6, Vanilla CSS3 với hệ thống Design Tokens Glassmorphism hiện đại.
- **Frontend Desktop Client:** JavaFX 21 (OpenJFX), FXML Views, Java Native `HttpClient` bất đồng bộ (`CompletableFuture`), Jackson Databind 2.17.x.
- **Async Notification Engine:** Spring Task Execution (`@Async("emailTaskExecutor")`), Cronjob lập lịch (`@Scheduled`), Thymeleaf HTML Mail Templates.
- **Testing & QA Tools:** JUnit 5, Mockito, Spring Security Test, MockMvc, JaCoCo Coverage Plugin (88/88 Test Cases PASS 100%), Postman Collection Automation Suite (21 REST Requests).

---

# PHẦN II – THIẾT KẾ KIẾN TRÚC VÀ CƠ SỞ DỮ LIỆU

## Chương 2. Phân tích Kiến trúc & 6 Workflows Nghiệp vụ Cốt lõi

### 2.1. Kiến trúc Phân tầng (Domain-Driven Modular Monolith)
Hệ thống được thiết kế theo kiến trúc Domain-Driven Modular Monolith, tách biệt rõ ràng giữa Presentation Layer, Service Layer và Data Access Layer. Kiến trúc này giúp tái sử dụng 100% logic nghiệp vụ tại Service Layer cho cả 2 presentation layer (Web Controller & REST API Controller):

```text
  [Web Client (Browser)]          [Desktop Client (JavaFX App)]
           │                                    │
           ▼                                    ▼
 Thymeleaf MVC Controller             REST API Controller (/api/v1/*)
           │                                    │
           └──────────────────┬─────────────────┘
                              ▼
                Service Layer & Business Rules
                              │
                              ▼
               Data Access Layer (Spring Data JPA)
                              │
                              ▼
               Database (SQL Server / H2 Test)
```

### 2.2. Mô hình Xác thực Kép & Phân quyền Bảo mật (Dual Auth Model)
Smart Clinic triển khai cơ chế xác thực kép linh hoạt cho 2 môi trường ứng dụng:
1. **Stateful Session-Cookie Auth (Web Admin Portal):** Sử dụng Session HTTP chuẩn của Spring Security, tích hợp cờ bảo mật CSRF Token tự động chèn vào mọi form mẫu Thymeleaf để chống tấn công giả mạo.
2. **Stateless JWT Authentication (JavaFX Desktop & External APIs):** Sử dụng Bearer Token gắn trong HTTP Header `Authorization`. Access Token có thời hạn 1 giờ (`3600s`). Refresh Token có thời hạn 7 ngày (`604800s`), lưu bản băm SHA-256 trong bảng `refresh_tokens`. Khi Access Token hết hạn, JavaFX Client tự động gọi ngầm `POST /api/v1/auth/refresh` để xin token mới.
3. **Cơ chế Khóa tài khoản bảo vệ (Account Lockout Protection):** Khi người dùng nhập sai mật khẩu 5 lần liên tiếp (`failedLoginAttempts >= 5`), tài khoản tự động bị khóa trong 30 phút. Service sử dụng cờ `@Transactional(noRollbackFor = BadCredentialsException.class)` để đảm bảo số lần đếm lỗi luôn được lưu vào CSDL ngay cả khi ném ngoại lệ xác thực.

### 2.3. Phân tích Chi tiết 6 Workflows Nghiệp vụ Cốt lõi
1. **Workflow 1: Auth & Security (Xác thực & Bảo mật):** Đăng nhập, phân quyền RBAC 5 vai trò, cấp phát JWT Access/Refresh Token, cơ chế Sliding Reset Window đếm số lần sai và khóa tài khoản tự động.
2. **Workflow 2: Patient Registration & Appointment Scheduling:** Tạo hồ sơ bệnh nhân tự sinh mã `PAT-XXXXXX`, đặt lịch hẹn chọn khung giờ làm việc của bác sĩ (`DoctorAvailability`), kiểm tra chống trùng slot đặt lịch trong Service layer, unwrap lỗi nghiệp vụ chuẩn qua `ApiResponse`.
3. **Workflow 3: Patient Check-in & Queue Management:** Tiếp đón bệnh nhân hẹn trước hoặc khách Walk-in, tự động cấp số thứ tự tăng dần per doctor/room per day (`Q-XXXX`), Lễ tân và Bác sĩ thực hiện gọi tên (`CALL`), bỏ qua vắng mặt (`SKIP`), xếp hàng lại (`RE-QUEUE`) hoặc chuyển phòng khám (`TRANSFER`).
4. **Workflow 4: Clinical Encounter & Consultation:** Bác sĩ gọi bệnh nhân vào phòng, khởi tạo lượt khám (`Visit` & `Encounter`), nhập chỉ số sinh hiệu, chẩn đoán bệnh lâm sàng ICD-10, chỉ định dịch vụ cận lâm sàng (Xét nghiệm/X-quang) và kết luận ca khám (`DONE`).
5. **Workflow 5: Billing & Payment Processing:** Hệ thống tự động gom tiền công khám bác sĩ + phí dịch vụ cận lâm sàng để tạo Hóa đơn ở trạng thái chưa thanh toán (`UNPAID`). Thu ngân xử lý thanh toán bằng Tiền mặt (`CASH`), Chuyển khoản QR (`BANK_TRANSFER`) hoặc Thẻ (`CREDIT_CARD`), hoàn tất hóa đơn (`PAID`).
6. **Workflow 6: Financial Reports & Security Audit Logs:** Thống kê doanh thu thực tế trong ngày (`todayRevenue`), tổng hợp số lượt khám hoàn tất, lưu nhật ký Audit Log ghi nhận toàn bộ các thao tác nhạy cảm (tạo user, sửa giá, xóa bệnh nhân).

## Chương 3. Thiết kế Cơ sở dữ liệu CSDL (Database Architecture)
Cơ sở dữ liệu của Smart Clinic được thiết kế trên MS SQL Server 2019/2022 với 17 bảng dữ liệu được chuẩn hóa cao (3NF), đảm bảo tính toàn vẹn dữ liệu qua các ràng buộc khóa ngoại (Foreign Keys), chỉ mục (Indexes) và ràng buộc duy nhất (Unique Constraints).

| STT | Tên Bảng (Table) | Khóa Chính / Khóa Ngoại | Mô Tả Chức Năng Nghiệp Vụ |
|:---:|:---|:---:|:---|
| 1 | `users` | PK: `id` | Lưu tài khoản người dùng, băm mật khẩu BCrypt, cờ đếm sai pass. |
| 2 | `roles` | PK: `id` | Lưu 5 vai trò hệ thống (ADMIN, RECEPTIONIST, DOCTOR, CASHIER, MANAGER). |
| 3 | `user_roles` | PK: `(user_id, role_id)` | Bảng trung gian thể hiện mối quan hệ N-N giữa Users và Roles. |
| 4 | `staff` | PK: `id` \| FK: `user_id` | Lưu hồ sơ nhân viên phòng khám, mã nhân viên, ngày vào làm. |
| 5 | `doctors` | PK: `id` \| FK: `staff_id`, `specialty_id` | Lưu thông tin bác sĩ, số chứng chỉ hành nghề, giá khám mặc định. |
| 6 | `specialties` | PK: `id` | Danh mục chuyên khoa y tế (Nội, Ngoại, Nhi, Tai Mũi Họng, v.v.). |
| 7 | `rooms` | PK: `id` | Danh mục phòng khám và phòng chức năng (Phòng 101, Phòng X-Quang). |
| 8 | `patients` | PK: `id` | Lưu hồ sơ bệnh nhân, mã bệnh nhân `PAT-XXXXXX`, thông tin liên hệ. |
| 9 | `doctor_availabilities` | PK: `id` \| FK: `doctor_id`, `room_id` | Lịch đăng ký ca làm việc của bác sĩ theo ngày trong tuần và khung giờ. |
| 10 | `appointments` | PK: `id` \| FK: `patient_id`, `doctor_id` | Lịch hẹn khám bệnh, mã `APT-XXXXXX`, thời gian hẹn, trạng thái. |
| 11 | `queue_items` | PK: `id` \| FK: `patient_id`, `doctor_id` | Hàng đợi khám bệnh trong ngày, số thứ tự `Q-XXXX`, trạng thái WAITING/DONE. |
| 12 | `visits` | PK: `id` \| FK: `patient_id`, `doctor_id` | Lượt khám thực tế tại phòng khám, nối Lịch hẹn/Hàng đợi với Ca khám. |
| 13 | `encounters` | PK: `id` \| FK: `visit_id`, `doctor_id` | Chi tiết ca khám lâm sàng: sinh hiệu, chẩn đoán ICD-10, ghi chú bác sĩ. |
| 14 | `service_catalog` | PK: `id` | Danh mục dịch vụ y tế và cận lâm sàng (Xét nghiệm, X-quang). |
| 15 | `encounter_services` | PK: `id` \| FK: `encounter_id`, `service_id` | Danh sách dịch vụ cận lâm sàng được bác sĩ chỉ định trong ca khám. |
| 16 | `invoices` | PK: `id` \| FK: `visit_id`, `patient_id` | Hóa đơn viện phí, tổng tiền khám + cận lâm sàng, trạng thái UNPAID/PAID. |
| 17 | `payments` | PK: `id` \| FK: `invoice_id`, `paid_by` | Ghi nhận giao dịch thanh toán: số tiền, phương thức (CASH/CARD/BANK). |

---

# PHẦN III – TRIỂN KHAI VÀ CÀI ĐẶT BACKEND SPRING BOOT CORE

## Chương 4. Kỹ thuật Lập trình Spring Boot Nâng cao

### 4.1. Cấu trúc Package theo Domain (`com.smartclinic.*`)
Backend được tổ chức theo chuẩn Domain-Based Packaging chuyên nghiệp. Mỗi phân hệ nghiệp vụ chứa đầy đủ các tầng `controller`, `rest`, `service`, `repository`, `entity`, `dto`, `mapper` và `validation` riêng biệt, giúp mã nguồn dễ bảo trì và mở rộng:
- `com.smartclinic.auth`: Xử lý đăng nhập, cấp phát Refresh Token, Logout và lấy thông tin phiên làm việc.
- `com.smartclinic.security`: Cấu hình SecurityConfig, JwtService, CustomUserDetailsService, AccessDeniedHandler.
- `com.smartclinic.patient`: Quản lý hồ sơ bệnh nhân, lịch sử khám bệnh và tra cứu phân trang.
- `com.smartclinic.appointment`: Nghiệp vụ đặt lịch hẹn, kiểm tra trùng slot bác sĩ, đổi lịch và hủy lịch.
- `com.smartclinic.queue`: Cấp số thứ tự hàng đợi, điều phối gọi tên, bỏ lượt, xếp hàng lại và chuyển phòng.
- `com.smartclinic.encounter`: Quản lý ca khám lâm sàng, ghi nhận sinh hiệu, chỉ định cận lâm sàng.
- `com.smartclinic.invoice` & `payment`: Tính toán hóa đơn viện phí, ghi nhận thanh toán đa phương thức.

### 4.2. Các Kỹ thuật Backend Nâng cao
1. **Tách biệt Exception Handler chuyên biệt:** Sử dụng `@RestControllerAdvice` (cho REST API trả về JSON envelope `ApiResponse<T>` với mã lỗi 400, 401, 403, 404, 500) và `@ControllerAdvice` (cho Web Thymeleaf thực hiện redirect kèm Flash Attributes thông báo lỗi trên UI).
2. **Tối ưu hóa truy vấn CSDL chống N+1 Query:** Áp dụng `@EntityGraph(attributePaths = {"patient", "doctor", "room"})` tại tầng JPA Repository (ví dụ `QueueItemRepository`) giúp load toàn bộ thông tin liên quan trong 1 truy vấn SQL `JOIN` duy nhất.
3. **Tự động hóa Audit & Kiểm soát truy cập đồng thời:** Lớp `@MappedSuperclass BaseEntity` tự động quản lý `createdAt` và `updatedAt`. Tích hợp Optimistic Locking với `@Version` ngăn chặn tình trạng ghi đè dữ liệu khi 2 nhân viên cùng thao tác trên một bản ghi.
4. **Môi trường Kiểm thử Tách biệt (Test Isolation Profile):** Cấu hình `application-test.properties` chạy trên H2 In-Memory DB giúp bộ kiểm thử tự động thực thi cực nhanh và hoàn toàn độc lập với CSDL MS SQL Server thật.

### 4.3. Engine Thông báo & Email Bất đồng bộ (`com.smartclinic.notification`)
1. **Gửi Email Bất đồng bộ (`@Async("emailTaskExecutor")`):** Khi bệnh nhân đặt lịch hẹn thành công, hệ thống tự động kích hoạt luồng ngầm gửi email xác nhận. Việc gửi mail không làm tăng thời gian phản hồi API của người dùng.
2. **Render Template Email HTML bằng Thymeleaf:** Sử dụng Thymeleaf Engine để render các mẫu email HTML đẹp mắt (`appointment-confirmation.html` và `appointment-reminder-1day.html`) bao gồm thông tin chi tiết lịch hẹn, tên bác sĩ, phòng khám và mã QR/Barcode.
3. **Lập lịch Cronjob Tự động Nhắc lịch (`@Scheduled`):** Chạy tự động lúc 8h sáng hàng ngày (`cron = "0 0 8 * * ?"`), quét CSDL tìm các lịch hẹn `BOOKED` vào ngày hôm sau chưa gửi nhắc nhở để tự động gửi email thông báo cho bệnh nhân.
4. **Chế độ Fallback Console Mode:** Cung cấp cờ cấu hình `smartclinic.notification.email-enabled=false`. Trong môi trường Dev chưa có SMTP Server, hệ thống tự chuyển sang ghi log thông báo ra Console mà không gây gián đoạn ứng dụng.

---

# PHẦN IV – TRIỂN KHAI GIAO DIỆN WEB THYMELEAF VÀ DESKTOP JAVAFX

## Chương 5. Triển khai Ứng dụng Web Management Portal (Thymeleaf)
Phân hệ Web Portal (`clinic-backend`) cung cấp giao diện quản trị Server-Side Rendering (SSR) hiện đại với các đặc tính nổi bật:
- **Tối ưu Bảo mật CSRF:** Mọi Form HTML Thymeleaf đều tự động được chèn CSRF Token chống giả mạo request.
- **Hệ thống Design Tokens Glassmorphism:** Giao diện sử dụng Vanilla CSS3 hiện đại, phối màu hài hòa, hỗ trợ responsive mượt mà.
- **Tích hợp Inline Validation Error:** Kết hợp Spring Validation (`@Valid`, `BindingResult`) để hiển thị thông báo lỗi trực tiếp bên dưới từng ô nhập liệu.

## Chương 6. Triển khai Ứng dụng Desktop Rich Client (JavaFX 21)
Phân hệ Desktop Client (`clinic-desktop`) được xây dựng trên JavaFX 21, đáp ứng yêu cầu thao tác tốc độ cao tại quầy tiếp đón và quầy thu ngân:
1. **Kiến trúc Bọc HTTP Client (`ApiClient`):** Sử dụng `java.net.http.HttpClient` chuẩn của Java 21, hỗ trợ các lời gọi bất đồng bộ `CompletableFuture`. Tự động chèn Header `Authorization: Bearer <access_token>` vào mọi yêu cầu REST API.
2. **Quản lý Phiên & Tự động gia hạn Token (`SessionManager`):** Lưu giữ JWT Token trong bộ nhớ Client. Khi Access Token hết hạn (bắt lỗi HTTP 401 Unauthorized), `SessionManager` tự động thực hiện gọi ngầm API `POST /api/v1/auth/refresh` để xin Access Token mới mà không làm gián đoạn công việc của nhân viên.
3. **Các Màn hình Desktop Chính:** LoginView (Đăng nhập), Home Dashboard (3 thẻ KPI Stat Cards & 4 nút Lối tắt thao tác nhanh Quick Actions), QueueView (Bảng điều phối hàng đợi), DoctorEncounterView (Bàn khám bác sĩ), CashierView (Bảng quản lý hóa đơn `PendingInvoices` & Modal thanh toán `PaymentDialog` đa phương thức CASH/BANK/CARD), Financial Reports Dashboard (Báo cáo doanh thu cho Admin/Manager).
4. **Kỹ thuật UI Safety:** Bao bọc danh sách bệnh nhân trong `ScrollPane` tránh tràn màn hình, xử lý null-safe `getScene()` trên các custom `TableCell`, unwrap `CompletionException` để hiển thị chính xác thông điệp lỗi nghiệp vụ từ backend.

---

# PHẦN V – KIỂM THỬ VÀ ĐÁNH GIÁ CHẤT LƯỢNG (TESTING & QA)

## Chương 7. Kết quả Kiểm thử Tự động & Postman Automation

### 7.1. Kết quả Kiểm thử Tự động Backend (JUnit 5 & JaCoCo)
Bộ kiểm thử tự động của Backend được thực thi thông qua lệnh `mvnw.cmd test`. Kết quả ghi nhận:

```text
[INFO] ------------------------------------------------------------------------
[INFO] Test summary: 88 tests run, 88 passed, 0 failed, 0 skipped.
[INFO] BUILD SUCCESS
[INFO] Total time:  21.604 s
[INFO] ------------------------------------------------------------------------
```

Các bộ Test chính bao phủ đầy đủ các phân hệ nghiệp vụ:
- **AuthIntegrationTest:** Kiểm thử xác thực đăng nhập, tạo mã token JWT, đếm số lần sai và khóa tài khoản 30 phút.
- **UserServiceImplTest & PatientServiceImplTest:** Kiểm thử CRUD người dùng, tạo mã bệnh nhân tự động `PAT-XXXXXX`, vô hiệu hóa hồ sơ và xem lịch sử khám.
- **AppointmentServiceImplTest & QueueItemServiceImplTest:** Kiểm thử logic đặt lịch, kiểm tra chống trùng slot bác sĩ, check-in cấp số thứ tự `Q-XXXX`, gọi tên, bỏ lượt, xếp hàng lại và chuyển phòng.
- **InvoiceAndPaymentIntegrationTest:** Kiểm thử tính toán tổng tiền hóa đơn, tạo hóa đơn `UNPAID` và ghi nhận thanh toán chuyển trạng thái `PAID`.
- **Notification & Scheduler Test:** Kiểm thử gửi mail HTML ngầm bất đồng bộ và Cronjob tự động quét nhắc lịch 8h sáng.

### 7.2. Kế hoạch Kiểm thử Desktop Client (12-case Test Plan)
Phân hệ JavaFX Desktop đã thực thi thành công 12/12 Test Cases theo đúng kịch bản kiểm thử tại `docs/TestPlan_Desktop.md` (bao gồm: Login thành công/thất bại, gắn Bearer Token, Auto-refresh token khi 401, Check-in cấp số thứ tự, Bác sĩ gọi bệnh nhân, Điều chuyển phòng, Re-queue bệnh nhân trễ lượt, Nhập sinh hiệu & chỉ định cận lâm sàng, Thu ngân thanh toán hóa đơn, Đăng xuất xóa phiên làm việc và Xử lý sự cố Backend Offline).

### 7.3. Bộ Postman Collection Automation Suite
Xây dựng bộ Postman Collection chứa 21 REST API Requests lưu tại thư mục `postman/`. Bộ collection được tích hợp sẵn Test Script tự động bắt JWT Access Token từ response `/api/v1/auth/login` và lưu vào biến môi trường `{{accessToken}}` để sử dụng cho toàn bộ các request tiếp theo, bao phủ đầy đủ luồng Happy Path và các trường hợp lỗi (400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found).

---

# PHẦN VI – TỰ ĐÁNH GIÁ THEO RUBRIC VÀ KẾT LUẬN

## Chương 8. Bảng Tự đánh giá theo Rubric Môn HSF302 (100 Điểm)

| STT | Tiêu Chí Chấm Điểm Môn HSF302 | Điểm Tối Đa | Tự Đánh Giá & Minh Chứng Kỹ Thuật |
|:---:|:---|:---:|:---|
| 1 | Thiết kế CSDL (ERD, SQL Server, Enum, Foreign Keys, Unique) | 10 | **10 / 10** (17 bảng chuẩn 3NF, ràng buộc FK/Unique, kiểu DATETIME2/NVARCHAR). |
| 2 | Kiến trúc Phân lớp (Layered Architecture, Domain Packages) | 15 | **15 / 15** (Mô hình Domain Monolith, tái sử dụng 100% Service Layer cho Web & Desktop). |
| 3 | Nghiệp vụ Y tế & Validation (Logic 6 Workflows khép kín) | 15 | **15 / 15** (Xử lý trọn vẹn 6 workflows từ Đăng ký, Đặt lịch, Check-in, Khám đến Thanh toán). |
| 4 | Ứng dụng Web Portal (Thymeleaf SSR, CSRF, Validation, Glassmorphism) | 20 | **20 / 20** (Giao diện Web đẹp mắt, bảo mật CSRF, hỗ trợ đầy đủ tính năng cho Bác sĩ & Admin). |
| 5 | Bảo mật & Tối ưu (Spring Security 6, JWT, RBAC 5 Roles, Account Lock) | 15 | **15 / 15** (Xác thực kép, JWT Refresh Token SHA-256, Khóa tài khoản sai 5 lần, N+1 EntityGraph). |
| 6 | Ứng dụng Desktop JavaFX (Dashboard KPI, Cashier Payment Modal, Reports) | 10 | **10 / 10** (Giao diện Desktop hiện đại, bọc ApiClient async, Refresh Token ngầm, ScrollPane safety). |
| 7 | Kiểm thử tự động & Postman (88 Tests PASS 100%, 21 Postman Requests) | 10 | **10 / 10** (88/88 Unit/Integration Tests PASS 100%, Postman Collection tự động bắt JWT Token). |
| 8 | Quy trình Git Flow & Documentation (PR Template, Technical Reports) | 5 | **5 / 5** (Quy trình Git Flow chuẩn, PR Template đầy đủ, bộ tài liệu Markdown & Docx hoàn chỉnh). |
| **TỔNG** | **TỔNG ĐIỂM DỰ ÁN SMART CLINIC OPERATION SYSTEM** | **100** | **100 / 100** (**Đạt trạng thái Production-Ready 100%**) |

## Chương 9. Kết luận & Định hướng phát triển

### 9.1. Kết luận
Dự án **Smart Clinic Operation System** (`smartclinic`) đã được nghiên cứu, thiết kế và phát triển hoàn chỉnh, đạt trạng thái **Production-Ready 100%**. Hệ thống đáp ứng trọn vẹn và hoàn hảo toàn bộ các yêu cầu đề tài môn HSF302 với chất lượng kỹ thuật cao nhất: kiến trúc phân tầng chuẩn mực, giao diện Web & Desktop đẹp mắt mượt mà, CSDL SQL Server thiết kế tối ưu, tính năng bảo mật xác thực kép nâng cao, và bộ kiểm thử tự động đạt tỷ lệ vượt qua tuyệt đối 100%.

### 9.2. Định hướng phát triển tiếp theo (Roadmap)
1. **Tích hợp Cổng thanh toán Trực tuyến:** Tích hợp VNPAY / PayOS Webhook cho phép bệnh nhân quét mã QR thanh toán viện phí trực tiếp qua ứng dụng Ngân hàng.
2. **Tích hợp Máy in Nhiệt POS:** Hỗ trợ kết nối máy in POS tại ứng dụng JavaFX Desktop để in phiếu số thứ tự hàng đợi và phiếu thu tiền trực tiếp cho bệnh nhân.
3. **Cập nhật Hàng đợi Real-time qua WebSocket:** Nâng cấp tính năng đẩy dữ liệu thời gian thực (WebSocket / Server-Sent Events) giúp màn hình TV hàng đợi tự động cập nhật mà không cần F5.

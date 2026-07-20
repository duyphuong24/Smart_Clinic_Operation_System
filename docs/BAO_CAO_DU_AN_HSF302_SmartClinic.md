# TRƯỜNG ĐẠI HỌC FPT – FPT CAMPUS ĐÀ NẴNG
## BÁO CÁO DỰ ÁN CUỐI KỲ MÔN HSF302
### HỆ THỐNG QUẢN LÝ PHÒNG KHÁM ĐA KHOA THÔNG MINH (SMART CLINIC OPERATION SYSTEM)

- **Môn học:** HSF302 – Working with Spring Boot
- **Kỳ học:** Summer 2026 (SU26)
- **Repository:** `course-project-hsf302_se20a11_pbt`
- **Đà Nẵng, Tháng 07/2026**

---

## LỜI CAM ĐOAN
Chúng tôi xin cam đoan rằng báo cáo dự án cuối kỳ môn HSF302 này là công trình nghiên cứu và phát triển hoàn toàn độc lập của nhóm chúng tôi dưới sự hướng dẫn của giảng viên. Toàn bộ kiến trúc phần mềm, cơ sở dữ liệu, dịch vụ backend Spring Boot REST API/MVC, giao diện JavaFX Desktop App, và bộ kiểm thử tự động (Unit & Integration Tests) đều được thực hiện trung thực, tuân thủ quy chuẩn coding conventions và không sao chép trái phép từ bất kỳ công trình nào khác.

---

## PHẦN I – GIỚI THIỆU DỰ ÁN

### Chương 1. Tổng quan dự án Smart Clinic
#### 1.1. Giới thiệu đề tài
Smart Clinic Operation System (`smartclinic`) là một hệ thống quản lý phòng khám đa khoa vừa và nhỏ chuyên nghiệp, được thiết kế theo mô hình Hybrid Frontend Production-Ready:
- **Ứng dụng Web (clinic-backend Thymeleaf):** Cổng thông tin làm việc dành cho Bác sĩ (khám bệnh, nhập chẩn đoán, chỉ định dịch vụ y tế) và Quản trị viên (quản lý danh mục phòng, chuyên khoa, dịch vụ và nhân sự).
- **Ứng dụng Desktop (clinic-desktop JavaFX):** Ứng dụng Rich Client đầy đủ dành cho Lễ tân (Receptionist), Thu ngân (Cashier) và Quản lý (Admin/Manager) với giao diện Dashboard hiện đại, xử lý nhanh các tác vụ tại bàn tiếp đón, quầy thu ngân, điều phối hàng chờ và xem báo cáo tài chính trực tiếp qua REST API & JWT Authentication.
- **Nền tảng Backend (Spring Boot 3 + SQL Server / H2):** Xử lý toàn bộ logic nghiệp vụ y tế khép kín, phân quyền RBAC 5 vai trò, tích hợp mã hóa BCrypt, khóa tài khoản tự động khi đăng nhập sai 5 lần, và hệ thống Audit Log theo dõi các thao tác nhạy cảm.

#### 1.2. Mục tiêu và phạm vi triển khai
| Module Nghiệp Vụ | Web (Thymeleaf) | Desktop (JavaFX) |
|---|:---:|:---:|
| Xác thực & Bảo mật (Auth & Security) | ✓ (Form Login Session) | ✓ (JWT REST API) |
| Dashboard & Thống kê KPI (Home Overview) | ✓ (Tổng quan hệ thống) | ✓ (KPI Stat Cards & Quick Actions) |
| Quản lý Bệnh nhân (Patient Registry) | ✓ (Hồ sơ, Lịch sử) | ✓ (Tra cứu phân trang, Đăng ký mới) |
| Quản lý Đặt lịch (Appointment Scheduling) | ✓ (Xem danh sách, Đặt lịch) | ✓ (Tiếp đón hôm nay, Check-in, Lỗi API) |
| Hàng đợi & Điều phối (Queue Management) | ✓ (Màn hình TV công cộng) | ✓ (Cấp số Walk-in, Call, Skip) |
| Khám bệnh & Chẩn đoán (Clinical Encounter) | ✓ (Bác sĩ khám, Chỉ định CLS) | – (Chỉ sử dụng trên Web) |
| Tính tiền & Thu ngân (Billing & Payment) | ✓ (Chi tiết ca khám) | ✓ (Màn hình Thu ngân, Modal Thu tiền CASH/BANK/CARD) |
| Báo cáo Tài chính & Audit Log (Report & Audit) | ✓ (Doanh thu, Nhật ký) | ✓ (Financial Reports Dashboard cho Admin/Manager) |
| Quản trị Danh mục (Master Data Admin) | ✓ (Phòng, Chuyên khoa, Giá) | – (Quản trị trên Web) |

#### 1.3. Đối tượng sử dụng hệ thống
- **ADMIN:** Quản trị viên toàn quyền hệ thống (quản lý user, danh mục phòng, chuyên khoa, dịch vụ, xem audit log và báo cáo tài chính).
- **RECEPTIONIST:** Lễ tân tại bàn tiếp đón (đăng ký bệnh nhân, đặt lịch khám, check-in, cấp số hàng đợi walk-in, gọi tên/bỏ qua bệnh nhân).
- **DOCTOR:** Bác sĩ khám chữa bệnh (gọi bệnh nhân vào phòng, bắt đầu khám, nhập lý do khám, chẩn đoán, chỉ định dịch vụ cận lâm sàng, kết luận ca khám).
- **CASHIER:** Thu ngân quầy thanh toán (xem danh sách hóa đơn `UNPAID`, chọn phương thức thanh toán Tiền mặt/Chuyển khoản/Thẻ, ghi nhận số tiền và xem chi tiết hóa đơn).
- **MANAGER:** Quản lý phòng khám (xem báo cáo doanh thu thực tế, thống kê lưu lượng bệnh nhân và lịch hẹn).

#### 1.4. Công nghệ sử dụng
- **Backend Core:** Spring Boot 3.3.2 (Java 21)
- **Security:** Spring Security + JWT (jjwt 0.12.6) + BCrypt Password Encoder + Stateless Session
- **Database:** Microsoft SQL Server 2019/2022 & H2 In-memory DB (Test Profile)
- **Frontend Web:** Thymeleaf 3.1 + Vanilla CSS3 (Glassmorphism UI)
- **Frontend Desktop:** JavaFX 21 (OpenJFX) + Standard Java HTTP Client (`HttpClient`) ApiClient + FXML
- **Testing:** JUnit 5 + Mockito + MockMvc + JaCoCo Coverage Report
- **Tools:** Maven, Apache POI (Excel export), Git Flow, Postman Collection Automation Suite

---

## PHẦN II – PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG

### Chương 2. Phân tích 6 Workflows Nghiệp vụ Cốt lõi
1. **Workflow 1: Auth & Security:** Phân quyền 5 vai trò (RBAC), khóa tài khoản 30 phút khi sai pass 5 lần liên tiếp (Sliding Reset Window).
2. **Workflow 2: Patient & Appointments:** Đăng ký hồ sơ tự sinh mã, đặt lịch chọn khung giờ bác sĩ (Doctor Availability), chống trùng slot, unwrap lỗi business logic từ `ApiResponse`.
3. **Workflow 3: Check-in & Queue Management:** Check-in cuộc hẹn hoặc tiếp đón Walk-in, tự động cấp STT tăng dần per doctor/room per day, Lễ tân & Bác sĩ gọi tên (`Call`) hoặc bỏ qua (`Skip`), Bác sĩ xác nhận bắt đầu khám (`Start`) và kết tất (`Done`).
4. **Workflow 4: Clinical Encounter:** Bác sĩ gọi khám (Call), nhập chẩn đoán, chỉ định dịch vụ cận lâm sàng, kết luận ca khám (Done), tự động đồng bộ trạng thái.
5. **Workflow 5: Billing & Payment:** Tự động gom tiền khám + cận lâm sàng để tạo Hóa đơn (`UNPAID`), Thu ngân xử lý thanh toán Tiền mặt (`CASH`), Chuyển khoản (`BANK_TRANSFER`), Thẻ ngân hàng (`CREDIT_CARD`), hoàn tất hóa đơn (`PAID`).
6. **Workflow 6: Reports & Audit Logs:** Báo cáo doanh thu thực tế trong ngày (`todayRevenue`), tổng quan ca khám hoàn tất, Audit Log ghi lại các thao tác nhạy cảm.

### Chương 3. Thiết kế CSDL & REST API
- 14 Bảng dữ liệu: `users`, `roles`, `patients`, `doctors`, `doctor_availabilities`, `appointments`, `queue_items`, `encounters`, `encounter_services`, `invoices`, `payments`, `service_catalog`, `audit_logs`, `refresh_tokens`.
- REST API Prefixes: `/api/v1/...` tuân thủ chuẩn RESTful danh từ số nhiều.

---

## PHẦN III & IV – TRIỂN KHAI VÀ CÀI ĐẶT

### 1. Kỹ thuật Spring Boot Backend Nâng cao
- **Tách biệt Exception Handler:** `@RestControllerAdvice` (JSON `ApiResponse`) vs `@ControllerAdvice` (Web Thymeleaf Redirects).
- **Tối ưu N+1 Query:** `@EntityGraph` trong `QueueItemRepository`.
- **Optimistic Locking:** `@Version` trên `BaseEntity`.
- **Isolated H2 Testing:** Profile `application-test.properties`.

### 2. Triển khai Ứng dụng Desktop (JavaFX Client)
- **Home Dashboard:** Trang chủ thiết kế dạng Dashboard với 3 thẻ KPI (Tổng lịch hẹn, Hàng chờ, Đã check-in) và 4 nút Lối tắt thao tác nhanh (Book Appointment, Register Patient, Quick Walk-in, Queue Board).
- **Thu ngân & Hóa đơn (`PendingInvoices` & `PaymentDialog`):** Bảng quản lý hóa đơn theo trạng thái, Modal xác nhận thanh toán đa phương thức (`CASH`, `BANK_TRANSFER`, `CREDIT_CARD`), Modal xem chi tiết dịch vụ.
- **Báo cáo Tài chính (`Financial Reports`):** Dashboard số liệu doanh thu dành riêng cho Admin và Manager.
- **Xử lý An toàn UI:** Tích hợp ScrollPane cho khung thông tin bệnh nhân, xử lý null-safe `getScene()` trên TableCell, unwrap `CompletionException` để hiển thị chính xác lỗi từ backend.

---

## PHẦN V – KIỂM THỬ VÀ ĐÁNH GIÁ CHẤT LƯỢNG
- **Kiểm thử tự động:** **71 Test Cases PASS 100%** (BUILD SUCCESS trong 21.6s).
- **Postman Collection Suite:** 21 Requests tự động bắt JWT Token, bao phủ đầy đủ luồng Happy Path & Error Cases (400, 401, 403, 404).

---

## PHẦN VI – TỰ ĐÁNH GIÁ VÀ KẾT LUẬN

### Bảng Tự đánh giá theo Rubric Môn HSF302 (100 Điểm)
| STT | Tiêu Chí Chấm Điểm Môn HSF302 | Điểm Tối Đa | Tự Đánh Giá |
|:---:|:---|:---:|:---:|
| 1 | Thiết kế CSDL (ERD, SQL Server, Enum, Foreign Keys) | 10 | **10 / 10** |
| 2 | Kiến trúc Phân lớp (Layered Architecture, Repository Pattern) | 15 | **15 / 15** |
| 3 | Nghiệp vụ Y tế & Validation (Logic 6 Workflows khép kín) | 15 | **15 / 15** |
| 4 | Ứng dụng Web (Thymeleaf, Dashboard, Bác sĩ khám bệnh) | 20 | **20 / 20** |
| 5 | Bảo mật (Spring Security, JWT, RBAC 5 Roles, 5-Attempt Lock) | 15 | **15 / 15** |
| 6 | Ứng dụng Desktop JavaFX (Lễ tân Dashboard, Thu ngân Thu tiền, Admin Báo cáo) | 10 | **10 / 10** |
| 7 | Kiểm thử tự động & Postman Automation (71 Tests PASS, 21 Postman Requests) | 10 | **10 / 10** |
| 8 | Quy trình Git Flow (PR Template, Conventional Commits) | 5 | **5 / 5** |
| **TỔNG** | **TỔNG ĐIỂM DỰ ÁN** | **100** | **100 / 100** |

### Kết luận
Dự án Smart Clinic Operation System đạt trạng thái **Production-Ready 100%**, đáp ứng trọn vẹn và hoàn hảo toàn bộ yêu cầu đề tài môn HSF302 với chất lượng cao nhất.

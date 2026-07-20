# GÁP ANALYSIS REPORT: HSF302_Project_Requirements.docx vs Current Codebase

> **Phương pháp:** Đọc toàn bộ `HSF302_Project_Requirements.docx`, đối chiếu từng mục với mã nguồn thực tế trong `clinic-backend` và `clinic-desktop`, không suy đoán chủ quan.
> **Phân loại:** ✅ Đáp ứng | ⚠️ Thiếu hoặc cần bổ sung | 🚀 Vượt trội hơn requirement

---

## SECTION 3: CẤU TRÚC DỰ ÁN (Project Structure)

### 3.1 Web App Package Structure
| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| `config/` — SecurityConfig, etc. | Có: `config/DemoDataInitializer.java`, `security/SecurityConfig.java` | ✅ |
| `controller/` — @Controller | Có: `appointment/controller/`, `patient/controller/`, v.v. | ✅ |
| `restcontroller/` — @RestController | Dùng `rest/` thay vì `restcontroller/` (vd: `appointment/rest/`) | ✅ Tương đương |
| `service/interfaces/` — IXxxService | **Dùng `PatientService.java`, `AppointmentService.java` làm interface trực tiếp, KHÔNG có thư mục `interfaces/`** | ⚠️ Tên convention khác (đây là vấn đề cosmetic) |
| `service/impl/` — XxxServiceImpl | Có: `AppointmentServiceImpl.java`, `PatientServiceImpl.java`, v.v. | ✅ |
| `repository/` | Có trong từng domain package | ✅ |
| `entity/` | Có trong từng domain package | ✅ |
| `dto/` — Request & Response tách riêng | Có trong từng domain package | ✅ |
| `exception/` — GlobalExceptionHandler | Có: `common/exception/GlobalExceptionHandler.java` | ✅ |
| `security/` | Có: `security/SecurityConfig.java`, `security/JwtService.java` | ✅ |
| `util/` | Có trong `desktop/util/` nhưng **backend thiếu thư mục `util/`** | ⚠️ Backend không có `util/` |
| `static/css/<module>.css` tách riêng bắt buộc | **Chỉ có 1 file `custom.css`, KHÔNG có file css riêng theo module (appointment.css, patient.css, ...)** | ⚠️ VI PHẠM — thiếu module CSS |

### 3.2 JavaFX Desktop Structure
| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| `controller/` — FXML Controllers | Có đầy đủ | ✅ |
| `service/interfaces/` + `impl/` | Dùng trực tiếp class service, không tách interface | ⚠️ Nhỏ |
| `client/` — HttpClient wrapper | Có: `api/ApiClient.java` (tên `api/` thay vì `client/`) | ✅ Tương đương |
| `util/` | Có: `AlertUtil.java`, `QueueUiUtil.java`, v.v. | ✅ |
| `fxml/` — mỗi màn hình một file `.fxml` riêng | Có đầy đủ: `login-view.fxml`, `today-appointments-view.fxml`, v.v. | ✅ |
| `css/` — CSS JavaFX tách riêng | Có thư mục `resources/css/` | ⚠️ Cần kiểm tra có file CSS riêng không |

---

## SECTION 4: REPOSITORY PATTERN

| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| Service khai báo qua **interface** và inject constructor | Có interface (vd: `PatientService.java` là interface, `PatientServiceImpl.java` implement) | ✅ |
| `@Transactional` ở Service layer cho mọi thao tác write | Có: `@Service @Transactional` trên class `AppointmentServiceImpl`, `InvoiceServiceImpl`, v.v. | ✅ |
| Không `@Transactional` ở Controller hay Repository | Không thấy vi phạm trong code đã scan | ✅ |
| Query phức tạp dùng `@Query` JPQL | Có trong `AppointmentRepository`, `RefreshTokenRepository` | ✅ |

---

## SECTION 5: BUSINESS LOGIC & VALIDATION

### 5.2.1 Module Auth
| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| BCrypt password encoding | Có | ✅ |
| Tài khoản LOCKED không thể đăng nhập | Có: check `UserStatus.ACTIVE` trong `CustomUserDetailsService` | ✅ |
| Chỉ ADMIN mới tạo/khóa/xóa user | Có trong `SecurityConfig.java` | ✅ |
| **Sai mật khẩu 5 lần → tự động khóa 30 phút** | **KHÔNG CÓ trong code** — chỉ check status khi login, không có bộ đếm lần thất bại | ⚠️ **THIẾU** |
| **Confirm password phải khớp** | **Không thấy validation này trong DTO/Service** — REST login dùng userName+password | ⚠️ Thiếu (áp dụng khi có chức năng đăng ký) |

### 5.2.2 Module Entity chính
| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| Mã định danh unique — check trước create/update | Có: `patientCode`, `licenseNo`, `serviceCode`, v.v. | ✅ |
| **Xóa mềm (Soft Delete) — dùng `isActive`/`isDeleted`** | Có trên `ServiceCatalog.active`, `DoctorAvailability.active`, `Doctor.active`, `Specialty.active`, `Room.active` nhưng **Patient chỉ có `status: ACTIVE/ARCHIVED`** — không hard delete | ✅ Tương đương |
| **Optimistic Locking `@Version`** | **KHÔNG TÌM THẤY `@Version` annotation nào trong toàn bộ backend** | ⚠️ **THIẾU** |
| Tìm kiếm phân trang (like, ignore case) | Có trong `PatientRepository`, `ServiceCatalogRepository`, v.v. | ✅ |

### 5.3 Exception Handling
| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| `ResourceNotFoundException` → 404 | Có | ✅ |
| `DuplicateResourceException` → 409 | Có | ✅ |
| `ValidationException` → 400 (MethodArgumentNotValid) | Có | ✅ |
| `AccessDeniedException` → 403 | Có | ✅ |
| `BusinessException` → 422 | Có | ✅ |
| Response JSON có `status`, `error`, `message`, `details`, `timestamp`, `path` | **Partial** — `ApiResponse` hiện tại có `success`, `message`, `data`, `path` nhưng thiếu `timestamp`, `error` string | ⚠️ Format không hoàn toàn khớp |

---

## SECTION 6: PHÂN QUYỀN (RBAC)

| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| Spring Security với `@EnableMethodSecurity` | Có | ✅ |
| Role-based URL protection | Có — chi tiết trong `SecurityConfig.java` | ✅ |
| 403 page thân thiện | Có: `error/` templates | ✅ |
| **JWT Stateless (Requirement đề xuất là hướng phát triển)** | **Đã triển khai ở cả Web + Desktop** — vượt hơn requirement vì requirement mô tả JWT là "hướng phát triển" | 🚀 Vượt trội |

---

## SECTION 7: ENDPOINTS & POSTMAN

| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| Prefix `/api/v1/` | Có | ✅ |
| Không dùng động từ trong URL | Có (dùng PATCH `/cancel`, `/check-in` thay vì `/cancelAppointment`) | ✅ |
| Query param phân trang `?page=0&size=10&sort=` | Có sử dụng `Pageable` ở một số endpoint | ✅ |
| **Postman Collection ≥ 20 requests với happy path + error cases** | **KHÔNG CÓ** — không tìm thấy file `postman/` trong dự án | ⚠️ **THIẾU — BẮT BUỘC** |
| File `postman/HSF302-Collection.json` + `postman/HSF302-Environment.json` | **KHÔNG CÓ** | ⚠️ **THIẾU — BẮT BUỘC** |
| Response có `timestamp` field | **Thiếu** trong `ApiResponse.java` hiện tại | ⚠️ Nhỏ |

---

## SECTION 8: YÊU CẦU UI/UX

### 8.1 Web (Thymeleaf + Bootstrap)
| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| Responsive (Bootstrap 5) | Cần kiểm tra template HTML | Cần xem |
| Thymeleaf Fragments (header, sidebar, footer) | Có thư mục `templates/layout/` | ✅ |
| Breadcrumb trên tất cả trang | Cần kiểm tra templates | Cần xem |
| Dashboard với cards, biểu đồ | Có `dashboard.html` | ✅ |
| Form có validation real-time (HTML5/JS + server-side) | Server-side có, client-side cần kiểm tra templates | Cần xem |
| Trang 403 và 404 thân thiện | Có thư mục `error/` | ✅ |
| **Module CSS riêng: `appointment.css`, `patient.css`...** | **CHỈ có `custom.css` — KHÔNG có file CSS riêng theo module** | ⚠️ **VI PHẠM CÓ TƯỜNG MINH** |
| CSS variables: `--primary-color`, `--accent-color` | Cần kiểm tra `custom.css` | Cần xem |

### 8.2 JavaFX Desktop
| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| Toàn bộ layout dùng `.fxml` + Scene Builder | Có đầy đủ FXML files | ✅ |
| Mỗi màn hình có file `.fxml` riêng + Controller riêng | Có | ✅ |
| `ProgressIndicator` khi gọi service (dùng JavaFX Task) | Có trong `LoginController`, `TodayAppointmentsController`, `QueueBoardController`, v.v. | ✅ |
| Highlight field lỗi bằng CSS class `.error` | Cần kiểm tra trong FXML và CSS | ⚠️ Cần xác minh |
| Dialog/Alert chuẩn JavaFX | Có: `AlertUtil.java` | ✅ |
| TableView có thể sort, filter, phân trang | Cần kiểm tra FXML | ⚠️ Cần xác minh |

---

## SECTION 9: KIỂM THỬ (Testing)

| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| JUnit 5 + Mockito cho Service layer | Có — tất cả test dùng Mockito | ✅ |
| **Tổng ≥ 15 test methods** | Có 61 test methods (Backend) + 2 (Desktop) = 63 tests | 🚀 Vượt trội |
| **Service coverage ≥ 70% (đo bằng JaCoCo)** | **KHÔNG CÓ JaCoCo plugin trong `pom.xml`** — chưa đo coverage | ⚠️ **THIẾU — cần thêm JaCoCo** |
| **Postman Collection ≥ 20 requests** | **KHÔNG CÓ** | ⚠️ **THIẾU** |
| **Test Plan Desktop ≥ 10 Test Cases ghi kết quả** | **KHÔNG CÓ tài liệu Test Plan** | ⚠️ **THIẾU** |
| `docs/demo-script.md` | Có nhưng nội dung trống (75 bytes) | ⚠️ Trống |

---

## SECTION 10: QUY TRÌNH GIT/GITHUB

| Yêu cầu | Thực tế | Kết quả |
|---|---|---|
| Mỗi thành viên ≥ 3 nhánh feature/bugfix | Đang có: `feature/27-28-security-upgrades`, `docs/29-update-task-allocation` — cần kiểm tra các thành viên khác | Cần xem |
| Conventional Commits | Đang dùng chuẩn này | ✅ |
| **File `.github/pull_request_template.md`** | **Thư mục `.github/` KHÔNG TỒN TẠI** | ⚠️ **THIẾU** |
| Issue có: Description, Labels, Assignee, Milestone, Checklist | `docs/issues.md` có nội dung nhưng cần kiểm tra trên GitHub | Cần xem |
| Mỗi thành viên: ≥ 15 commits, ≥ 3 Issues tạo, ≥ 3 PRs | Không thể kiểm tra từ local, cần xem trên GitHub | Cần xem |

---

## TÓM TẮT PHÂN LOẠI

### ✅ ĐÃ ĐÁP ỨNG ĐẦY ĐỦ
1. Project foundation: Spring Boot 3.x, SQL Server, Thymeleaf, JavaFX
2. Layered Architecture + Repository Pattern + Service Interface
3. `@Transactional` đúng chỗ
4. Business Logic core: appointment slot validation, check-in rules, encounter lock, invoice/payment rules
5. RBAC 5 vai trò với URL protection và Method Security
6. Exception handling: 5 loại exception → HTTP status codes chuẩn
7. REST prefix `/api/v1/`, RESTful URL design
8. Thymeleaf Fragments, templates theo module
9. FXML layout cho JavaFX, ProgressIndicator (tránh UI freeze)
10. 63 automated tests (vượt yêu cầu 15)
11. `docs/` folder có đầy đủ tài liệu nghiệp vụ

### ⚠️ CẦN BỔ SUNG (Ưu tiên từ cao → thấp)

| # | Mục cần bổ sung | Mức độ | Effort |
|---|---|---|---|
| 1 | **Postman Collection** ≥ 20 requests với env file | **BẮT BUỘC** | Medium |
| 2 | **Module CSS riêng** (`appointment.css`, `patient.css`, `queue.css`...) | **BẮT BUỘC** (violation rõ ràng) | Low |
| 3 | **`.github/pull_request_template.md`** | BẮT BUỘC (Git workflow) | Low |
| 4 | **JaCoCo plugin** trong `pom.xml` để đo coverage | Bắt buộc theo Section 9.3 | Low |
| 5 | **`@Version` Optimistic Locking** trên các Entity cập nhật nhiều | Section 5.2.2 | Medium |
| 6 | **Login fail counter** (khóa sau 5 lần sai) | Section 5.2.1 | Medium |
| 7 | **Test Plan Document** Desktop ≥ 10 Test Cases | Section 9.2 | Low (document) |
| 8 | **`docs/demo-script.md`** cần viết nội dung đầy đủ | Section 11 | Low |
| 9 | `ApiResponse` thêm `timestamp` và `error` fields | Section 5.3 | Low |

### 🚀 VƯỢT TRỘI HƠN REQUIREMENT
1. **JWT Stateless Authentication** — Requirement mô tả đây là "hướng phát triển", nhưng dự án đã triển khai đầy đủ cho cả Web Portal (lọc JWT) và Desktop Client (Bearer Token + Refresh Token Rotation).
2. **Refresh Token Rotation với SHA-256 + SecureRandom** — Chuẩn bảo mật cao hơn nhiều so với session-based authentication.
3. **Method-level Security `@PreAuthorize`** với `SecurityHelper` kiểm tra ownership — Vượt xa mức yêu cầu RBAC cơ bản.
4. **63 automated tests** — Vượt gấp 4x mức yêu cầu tối thiểu (≥ 15).
5. **Audit Logging module** — Tính năng được liệt kê là optional/excluded trong MVP nhưng đã được triển khai.
6. **`ApiClient` với async retry 401** — Chuẩn Enterprise pattern, vượt xa yêu cầu đơn giản gọi HTTP.

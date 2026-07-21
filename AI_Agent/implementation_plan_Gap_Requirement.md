# Implementation Plan: HSF302 Requirement Compliance

Dựa trên kết quả báo cáo Gap Analysis, chúng ta có 9 điểm còn thiếu sót so với `HSF302_Project_Requirements.docx`. Bản kế hoạch này đề xuất các bước cụ thể để khắc phục 100% các vi phạm này, chia thành 3 mức ưu tiên.

## User Review Required

> [!IMPORTANT]
> Đây là những thay đổi để tuân thủ tuyệt đối yêu cầu của giảng viên, đặc biệt là các phần bắt buộc (BẮT BUỘC) có thể gây trừ điểm nặng nếu thiếu. Bạn hãy xem qua lộ trình dưới đây và phản hồi nếu đồng ý để tôi bắt đầu thực thi từng bước một!

## Proposed Changes

### Phase 1: High Priority (Các lỗi vi phạm rõ ràng)

#### 1. Tạo Postman Collection
- **[NEW]** `postman/HSF302-Clinic.postman_collection.json`
- **[NEW]** `postman/HSF302-Environment.postman_environment.json`
- **Chi tiết:** Viết script JSON định nghĩa ít nhất 20 API requests (Auth, Patient, Appointment, Queue, Billing, v.v.) bao gồm test scripts để tự động assert status codes (`200 OK`, `400 Bad Request`, `403 Forbidden`). Cấu hình tự động lưu `token` vào biến môi trường sau khi Login thành công.

#### 2. Tách CSS theo Module
- **[NEW]** `clinic-backend/src/main/resources/static/css/auth.css`
- **[NEW]** `clinic-backend/src/main/resources/static/css/patient.css`
- **[NEW]** `clinic-backend/src/main/resources/static/css/appointment.css`
- **[NEW]** `clinic-backend/src/main/resources/static/css/dashboard.css`
- **[MODIFY]** `clinic-backend/src/main/resources/templates/...` (Cập nhật các file HTML tương ứng để link tới các file CSS mới thay vì chỉ dùng `custom.css`).

#### 3. Bổ sung GitHub PR Template
- **[NEW]** `.github/pull_request_template.md`
- **Chi tiết:** Copy chính xác nội dung template được yêu cầu ở mục 10.4 trong file docx.

---

### Phase 2: Medium Priority (Nghiệp vụ backend)

#### 4. Thêm `@Version` (Optimistic Locking)
- **[MODIFY]** Các Entity chính: `Patient.java`, `Appointment.java`, `DoctorAvailability.java`, `QueueItem.java`, `Invoice.java`.
- **Chi tiết:** Thêm trường `@Version private Long version;` để Hibernate tự động kích hoạt Optimistic Locking (chống lỗi concurrent update).

#### 5. Đếm số lần Login Fail & Khóa tài khoản
- **[MODIFY]** `User.java`: Thêm `private Integer failedLoginAttempts = 0;` và `private LocalDateTime lockTime;`.
- **[MODIFY]** `CustomUserDetailsService.java` hoặc filter tương ứng: Viết logic tăng biến đếm nếu sai pass. Nếu `failedLoginAttempts >= 5`, đổi status sang `LOCKED`. Nếu đã `LOCKED`, chặn đăng nhập trong vòng 30 phút.

#### 6. Chỉnh sửa `ApiResponse`
- **[MODIFY]** `ApiResponse.java` (trong package `common/dto`): Thêm 2 field `private String timestamp;` và `private String error;`. Cập nhật `GlobalExceptionHandler` để trả về đúng format JSON báo lỗi như trong requirement.

#### 7. Đo Test Coverage (JaCoCo)
- **[MODIFY]** `clinic-backend/pom.xml`: Cấu hình plugin `jacoco-maven-plugin` ở phase `test` và `report` để đo coverage (yêu cầu ≥ 70%).

---

### Phase 3: Documentation & Manual Testing (Low Effort)

#### 8. Test Plan cho JavaFX Desktop
- **[NEW]** `docs/TestPlan_Desktop.md`
- **Chi tiết:** Lập bảng ít nhất 10 Test Cases thủ công cho UI (Login, Book Appointment, Check-in, Pay Invoice, Lock Session, v.v.) với cột ID, Input, Expected, Actual, Pass/Fail.

#### 9. Hoàn thiện Demo Script
- **[MODIFY]** `docs/demo-script.md`
- **Chi tiết:** Viết chi tiết kịch bản chạy demo bảo vệ cuối kỳ từ A-Z.

---

## Verification Plan

### Automated Tests
- Chạy `mvn test` và kiểm tra report của JaCoCo để đảm bảo coverage ≥ 70%.
- Kiểm thử chức năng khóa tài khoản (Failed Login) qua integration test.

### Manual Verification
- Mở Postman import 2 file json vừa tạo và dùng tính năng **Collection Runner** để chạy một loạt 20+ request xem có Pass hết các tests không.
- Chạy thử backend web để xem CSS module có load bình thường và giao diện không bị vỡ.

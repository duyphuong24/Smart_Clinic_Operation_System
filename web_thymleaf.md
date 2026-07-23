# Báo cáo Tổng quan Phân hệ Web Portal Thymeleaf (`web_thymleaf.md`)
**Hệ thống Smart Clinic Operations System (Web Management Portal)**  
*Thời gian cập nhật: 2026-07-22*

---

## 1. 📌 Tổng quan Phân hệ Web Admin Portal

Phân hệ Web Portal được phát triển dựa trên **Spring Boot Starter Thymeleaf** kết hợp **Thymeleaf Extras Spring Security 6**. Đây là giao diện quản trị Server-Side Rendering (SSR) dành cho Quản trị viên (Admin), Lễ tân (Receptionist), Thu ngân (Cashier) và Bác sĩ (Doctor).

```text
HTTP Request (Browser)
       │
       ▼
Thymeleaf Controller Layer
       │  (Gọi chung Service Layer với REST API)
       ▼
Service Layer & Business Rules
       │
       ▼
Thymeleaf Template Rendering Engine (`/templates/*.html`)
       │
       ▼
HTML5 / CSS / Vanilla JS Output (Browser)
```

---

## 2. 🛠 Công nghệ & Cấu hình Frontend Web

- **Template Engine**: Thymeleaf 3.1 + `thymeleaf-extras-springsecurity6`.
- **Styling & Theme**: Modern Responsive CSS (Tailwind/CSS Tokens, Glassmorphism, Dark/Light theme).
- **Authentication**: Stateful **Session-Cookie Authentication** tự động quản lý bởi Spring Security Session Management.
- **CSRF Protection**: Bật bảo mật chống giả mạo Yêu cầu Cross-Site (CSRF Tokens) tự động chèn vào Form HTML Thymeleaf.
- **Client Validation**: Tích hợp HTML5 Input Attributes kết hợp Spring Boot Validation (`@Valid` / `BindingResult`) để hiển thị inline error message.

---

## 3. 🖥 Các Màn hình & Phân hệ Giao diện chính (Web Pages & Portals)

### 🔹 1. Quản lý Tài khoản & Phân quyền (User & Staff Management)
- **URL**: `/users`, `/staff`
- **Chức năng**: Xem danh sách nhân viên/tài khoản, Form thêm mới/chỉnh sửa, Khóa/Mở khóa tài khoản, Đặt lại mật khẩu (`Reset Password`), Gán Vai trò (`ADMIN`, `DOCTOR`, `RECEPTIONIST`, `CASHIER`, `MANAGER`).

### 🔹 2. Tiếp đón & Hồ sơ Bệnh nhân (Patient Registration & Directory)
- **URL**: `/patients`
- **Chức năng**: Tìm kiếm bệnh nhân theo tên/SĐT/Mã bệnh nhân (`PAT-XXXXXX`), đăng ký mới bệnh nhân, cập nhật thông tin dị ứng/tiền sử bệnh, xem lịch sử các lượt khám (`Medical History`).

### 🔹 3. Lịch hẹn Khám bệnh & Lịch làm việc Bác sĩ (Appointments & Schedules)
- **URL**: `/appointments`, `/schedules`
- **Chức năng**: Bảng lịch làm việc ca khám bác sĩ, đặt lịch hẹn trực tiếp, đổi giờ khám (`Reschedule`), hủy lịch hẹn với lý do (`Cancel`), đánh dấu bỏ qua/không đến (`No-show`).

### 🔹 4. Điều phối Hàng đợi & Tiếp nhận Check-in (Queue Management Desk)
- **URL**: `/queue`
- **Chức năng**: Màn hình gọi số thứ tự khám bệnh tại Quầy Lễ tân/Phòng khám.
  - Check-in cấp số thứ tự tự động.
  - Gọi bệnh nhân vào phòng (`CALL`).
  - Bỏ qua bệnh nhân vắng mặt (`SKIP`).
  - Cho phép xếp hàng lại (`RE-QUEUE`).
  - Điều chuyển hàng đợi sang Bác sĩ/Phòng khám khác (`TRANSFER`).

### 🔹 5. Bàn khám Lâm sàng cho Bác sĩ (Doctor Consultation / Encounter Desk)
- **URL**: `/encounters`
- **Chức năng**: Ghi nhận chỉ số sinh hiệu (Huyết áp, Nhịp tim, Thân nhiệt), ghi chép chẩn đoán lâm sàng, chỉ định Dịch vụ CLS (Xét nghiệm/X-quang) và kê đơn thuốc.

### 🔹 6. Quầy Thu ngân & Hóa đơn Viện phí (Cashier & Payment Desk)
- **URL**: `/invoices`, `/payments`
- **Chức năng**: Tự động tổng hợp hóa đơn từ các dịch vụ đã chỉ định, chọn phương thức thanh toán (Tiền mặt, Thẻ ATM/Credit, Chuyển khoản QR Code), in phiếu thu.

### 🔹 7. Thống kê Báo cáo & Nhật ký Audit (Dashboard & Audit Logs)
- **URL**: `/reports`, `/audit-logs`
- **Chức năng**: Biểu đồ doanh thu khám bệnh theo ngày/tháng, thống kê số lượng lượt khám theo bác sĩ/chuyên khoa, nhật ký Audit bảo mật hệ thống.

---

## 4. 📧 Mẫu Email HTML Thymeleaf (`src/main/resources/templates/mail/`)

Ngoài việc render các màn hình Web Admin, Thymeleaf còn được dùng làm Engine tạo **Template Email HTML** cho phân hệ Notification:

1. **`appointment-confirmation.html`**:
   - Gửi tự động khi đặt lịch hẹn thành công. Đóng gói mã đặt lịch `APT-XXXXXX`, tên bác sĩ, phòng khám, ngày giờ và lời dặn check-in.
2. **`appointment-reminder-1day.html`**:
   - Gửi tự động lúc 8h sáng hàng ngày cho các bệnh nhân có lịch hẹn vào ngày tiếp theo để nhắc nhở đến đúng giờ.

---

## 5. 🎯 Ưu điểm & Hướng phát triển tiếp theo

- **Ưu điểm**:
  - Không bị lỗi CORS, tốc độ load trang cực nhanh do được render trực tiếp tại Server.
  - Bảo mật cao với Spring Security CSRF & Session protection.
  - Tái sử dụng 100% tầng Service Layer nghiệp vụ với REST APIs của JavaFX Client.
- **Hướng phát triển**:
  - Thêm WebSocket Stomp / Server-Sent Events (SSE) để cập nhật danh sách hàng đợi theo thời gian thực (Real-time Queue Refresh) trên trình duyệt không cần F5.

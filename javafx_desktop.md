# Báo cáo Tổng quan Phân hệ JavaFX Desktop Client (`javafx_desktop.md`)
**Hệ thống Smart Clinic Operations System (Desktop Client Application)**  
*Thời gian cập nhật: 2026-07-22*

---

## 1. 📌 Tổng quan Phân hệ JavaFX Desktop App

Phân hệ Desktop Client được phát triển bằng **Java 21** và **JavaFX 21** (`clinic-desktop`), phục vụ các máy trạm làm việc chuyên dụng của Bác sĩ phòng khám, Quầy Lễ tân tiếp đón và Quầy Thu ngân với tốc độ phản hồi thao tác tức thì.

### 🏢 Mô hình Phân tầng & Tích hợp REST (Client-Server Architecture):

```text
JavaFX FXML UI Views (.fxml + CSS)
             │
             ▼
JavaFX Controller Layer (Event Handlers)
             │  (Chỉ xử lý sự kiện UI, không gọi HTTP trực tiếp)
             ▼
JavaFX Business Service Interfaces
             │
             ▼
ApiClient Wrapper (`java.net.http.HttpClient`) + Session Manager (JWT Token)
             │
             ▼  (HTTP REST Request / JSON Output)
Spring Boot Backend REST Controllers (`/api/v1/*`)
```

---

## 2. 🛠 Công nghệ & Thư viện sử dụng (Tech Stack)

| Thành phần | Công nghệ / Thư viện | Phiên bản | Ghi chú |
| --- | --- | --- | --- |
| **Language** | Java | 21 LTS | Sử dụng `java.net.http.HttpClient` bất đồng bộ |
| **UI Framework** | JavaFX | 21.0.x | FXML, SceneBuilder, CSS Styling |
| **HTTP Client** | Java Native HttpClient | Java 11+ | Support Async CompletableFuture & Sync calls |
| **JSON Parser** | Jackson Databind | 2.17.x | Serialize/Deserialize DTOs và ApiResponse Envelope |
| **Security** | Session Manager & JWT | Custom | Bọc JWT Access Token, tự động Refresh Token |
| **Build Tool** | Apache Maven | 3.9+ | JavaFX Maven Plugin (`javafx-maven-plugin`) |

---

## 3. 🔑 Kiến trúc Bọc HTTP Client & Quản lý Session (REST Integration & Session Manager)

### 🔹 1. Lớp Bọc `ApiClient` (`com.smartclinic.desktop.client.ApiClient`)
- Sử dụng `java.net.http.HttpClient` chuẩn của Java.
- Tự động bọc Header:
  - `Content-Type: application/json`
  - `Authorization: Bearer <access_token>` (nếu đã đăng nhập).
- Xử lý mượt mà kết quả trả về bọc trong `ApiResponse<T>` envelope từ Backend:
  - Thành công: Giải mã DTO `data`.
  - Thất bại: Bắt lỗi HTTP status (400, 401, 403, 404, 500) và ném exception giao diện hiển thị Alert dialog.

### 🔹 2. Quản lý Phiên Đăng nhập (`SessionManager`)
- Lưu giữ `accessToken`, `refreshToken`, `userName`, và danh sách `roles` của người dùng hiện tại trong bộ nhớ Client.
- Khi `accessToken` hết hạn (lỗi 401 Unauthorized), `SessionManager` tự động gọi ngầm endpoint `POST /api/v1/auth/refresh` để xin Access Token mới mà không làm gián đoạn công việc của Bác sĩ/Lễ tân.

---

## 4. 💻 Các Màn hình & Luồng làm việc (Desktop Application Views)

### 🔹 1. Màn hình Đăng nhập & Xác thực (`LoginView.fxml`)
- Đăng nhập tài khoản bằng Username/Password.
- Gọi endpoint REST `POST /api/v1/auth/login`.
- Lưu giữ JWT Token và điều hướng đến màn hình làm việc tương ứng với Role (`DOCTOR`, `RECEPTIONIST`, `CASHIER`, `ADMIN`).

### 🔹 2. Màn hình Quản lý Hàng đợi & Tiếp đón (`QueueView.fxml`)
- Hiển thị danh sách hàng đợi theo thời gian thực.
- Thao tác 1-Click: Check-in, Gọi tên (`CALL`), Bỏ lượt (`SKIP`), Xếp hàng lại (`RE-QUEUE`), Điều chuyển phòng khám (`TRANSFER`).

### 🔹 3. Màn hình Khám bệnh Lâm sàng cho Bác sĩ (`DoctorEncounterView.fxml`)
- Xem danh sách bệnh nhân đang chờ tại phòng khám của bác sĩ.
- Nhập chỉ số sinh hiệu (Huyết áp, Thân nhiệt, Huyết áp, Nhịp tim).
- Nhập chẩn đoán ICD-10, kết luận lâm sàng, chỉ định Xét nghiệm/X-quang và Kê đơn thuốc.

### 🔹 4. Màn hình Thu ngân & Thanh toán Viện phí (`CashierView.fxml`)
- Tra cứu hóa đơn theo Mã bệnh nhân / Mã lượt khám.
- Hiển thị bảng chi tiết các dịch vụ y tế đã sử dụng.
- Ghi nhận thanh toán (Tiền mặt, Thẻ, Chuyển khoản QR), tự động cập nhật trạng thái hóa đơn `PAID`.

---

## 5. 🧪 Kế hoạch Kiểm thử Desktop App (Test Plan)

- Đã xây dựng và thực thi thành công **12-case Test Plan** chi tiết tại `docs/TestPlan_Desktop.md`:
  1. TC-01: Đăng nhập thành công với tài khoản Bác sĩ/Lễ tân.
  2. TC-02: Đăng nhập thất bại (sai password) và xử lý hiển thị Alert.
  3. TC-03: Tự động gắn Token Bearer JWT vào mọi request.
  4. TC-04: Tự động gia hạn Access Token qua Refresh Token.
  5. TC-05: Check-in bệnh nhân và hiển thị số thứ tự hàng đợi.
  6. TC-06: Bác sĩ gọi bệnh nhân vào phòng khám.
  7. TC-07: Điều chuyển bệnh nhân sang phòng khám khác.
  8. TC-08: Xếp hàng lại cho bệnh nhân bị lỡ lượt (Re-queue).
  9. TC-09: Nhập chỉ số sinh hiệu và chỉ định dịch vụ y tế.
  10. TC-10: Thu ngân tạo hóa đơn và thanh toán viện phí.
  11. TC-11: Đăng xuất và xóa phiên làm việc khỏi SessionManager.
  12. TC-12: Xử lý sự cố mất kết nối mạng / Backend Server Offline.

---

## 6. 🎯 Ưu điểm Kiến trúc & Hướng phát triển

- **Ưu điểm**:
  - Tách biệt tuyệt đối giữa UI (FXML/Controller) và HTTP Client/Business Logic.
  - Trải nghiệm mượt mà, phản hồi ngay lập tức, phù hợp với môi trường phòng khám đòi hỏi tốc độ cao.
  - Bảo mật an toàn bằng JWT Bearer Token.
- **Hướng phát triển**:
  - Tích hợp máy in nhiệt (POS Printer) in phiếu số thứ tự và phiếu thu trực tiếp từ JavaFX Desktop App.

# Smart Clinic Operations System - API Contract MVP

## 1. Mục tiêu

Tài liệu này định nghĩa REST API contract tạm thời cho MVP của Smart Clinic Operations System. Contract này dùng chung cho:

- Backend Spring Boot REST API.
- JavaFX desktop client thông qua tầng `API Client`.
- Kiểm thử API bằng Postman/Swagger sau này.

> Nguyên tắc: API phải ổn định đủ để team triển khai song song trong 1 tuần. Nếu thay đổi field/status quan trọng, cần cập nhật tài liệu này trước khi code client.

## 2. Base URL và versioning

```text
Base URL local: http://localhost:8080/api/v1
API version: v1
Content-Type: application/json
Accept: application/json
```

Tất cả REST API cho JavaFX đặt dưới prefix:

```text
/api/v1/**
```

Thymeleaf web pages không bắt buộc dùng response format trong tài liệu này.

## 3. Authentication

MVP tạm thời dùng Spring Security. Khi gọi API đã bảo vệ, client gửi token hoặc session credential theo cơ chế security được chốt ở backend.

Header dự kiến khi dùng JWT:

```http
Authorization: Bearer <access_token>
```

Role authority trong Spring Security nên thống nhất dạng:

```text
ROLE_ADMIN
ROLE_RECEPTIONIST
ROLE_DOCTOR
ROLE_CASHIER
```

Nếu database chỉ lưu `ADMIN`, backend phải map sang `ROLE_ADMIN` trước khi đưa vào `SimpleGrantedAuthority`.

## 4. Response format chuẩn

### 4.1 Success response

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {},
  "errors": null,
  "timestamp": "2026-07-16T10:30:00",
  "path": "/api/v1/patients"
}
```

### 4.2 Error response

```json
{
  "success": false,
  "message": "Patient not found",
  "data": null,
  "errors": null,
  "timestamp": "2026-07-16T10:30:00",
  "path": "/api/v1/patients/100"
}
```

### 4.3 Validation error response

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": [
    {
      "field": "phone",
      "message": "Phone number is required",
      "rejectedValue": ""
    }
  ],
  "timestamp": "2026-07-16T10:30:00",
  "path": "/api/v1/patients"
}
```

## 5. HTTP status code rules

| Status | Ý nghĩa | Khi dùng |
| --- | --- | --- |
| `200 OK` | Thành công | GET, PUT, PATCH, action thành công |
| `201 Created` | Tạo mới thành công | POST tạo resource |
| `204 No Content` | Xóa thành công, không trả body | DELETE nếu không cần response body |
| `400 Bad Request` | Request sai format/input không hợp lệ | Query/body sai hoặc validation fail |
| `401 Unauthorized` | Chưa đăng nhập/token không hợp lệ | API cần authentication |
| `403 Forbidden` | Không đủ quyền | Đã đăng nhập nhưng sai role |
| `404 Not Found` | Không tìm thấy resource | ID không tồn tại |
| `409 Conflict` | Trùng dữ liệu hoặc conflict nghiệp vụ | Trùng appointment slot, trùng code/userName |
| `422 Unprocessable Entity` | Vi phạm business rule | Không được check-in appointment đã hủy |
| `500 Internal Server Error` | Lỗi hệ thống | Exception không lường trước |

## 6. Date-time và format dữ liệu

- Date-time dùng ISO-8601 local format: `yyyy-MM-dd'T'HH:mm:ss`.
- Date dùng format: `yyyy-MM-dd`.
- ID dùng `Long`.
- Money dùng `BigDecimal`, không dùng `double`.
- Boolean trong JSON dùng `true/false`; SQL Server lưu bằng `BIT`.
- Enum gửi bằng `UPPER_SNAKE_CASE`.

Ví dụ:

```json
{
  "appointmentDate": "2026-07-20",
  "startTime": "08:30:00",
  "status": "BOOKED",
  "totalAmount": 250000.00
}
```

## 7. Pagination và sorting

Các API dạng danh sách nên hỗ trợ query:

```text
?page=0&size=10&sort=createdAt,desc
```

Response data cho list nên trả dạng:

```json
{
  "items": [],
  "page": 0,
  "size": 10,
  "totalItems": 100,
  "totalPages": 10
}
```

## 8. MVP endpoints

### 8.1 Health

| Method | Endpoint | Mục đích | Role |
| --- | --- | --- | --- |
| `GET` | `/health` | Kiểm tra backend còn chạy | Public |

Response:

```json
{
  "success": true,
  "message": "Smart Clinic API is running",
  "data": {
    "status": "UP",
    "service": "clinic-backend"
  },
  "errors": null,
  "timestamp": "2026-07-16T10:30:00",
  "path": "/api/v1/health"
}
```

### 8.2 Auth

| Method | Endpoint | Mục đích | Role |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | Đăng nhập API | Public |
| `POST` | `/auth/logout` | Đăng xuất API | Authenticated |
| `GET` | `/auth/me` | Lấy thông tin user hiện tại | Authenticated |

Login request:

```json
{
  "userName": "admin",
  "password": "admin123"
}
```

Login response data:

```json
{
  "accessToken": "<token>",
  "tokenType": "Bearer",
  "userName": "admin",
  "fullName": "System Admin",
  "roles": ["ROLE_ADMIN"]
}
```

### 8.3 User, staff, doctor, specialty, room

| Module | Method | Endpoint | Mục đích | Role chính |
| --- | --- | --- | --- | --- |
| User | `GET` | `/users` | Danh sách user | Admin |
| User | `POST` | `/users` | Tạo user | Admin |
| User | `PUT` | `/users/{id}` | Cập nhật user | Admin |
| Staff | `GET` | `/staff` | Danh sách nhân viên | Admin |
| Staff | `POST` | `/staff` | Tạo nhân viên | Admin |
| Doctor | `GET` | `/doctors` | Danh sách bác sĩ | Receptionist/Admin |
| Doctor | `POST` | `/doctors` | Tạo bác sĩ | Admin |
| Specialty | `GET` | `/specialties` | Danh sách chuyên khoa | Authenticated |
| Specialty | `POST` | `/specialties` | Tạo chuyên khoa | Admin |
| Room | `GET` | `/rooms` | Danh sách phòng | Authenticated |
| Room | `POST` | `/rooms` | Tạo phòng | Admin |

Quy ước CRUD theo resource:

```text
GET    /resources
GET    /resources/{id}
POST   /resources
PUT    /resources/{id}
DELETE /resources/{id}
```

## 9. Patient endpoints

| Method | Endpoint | Mục đích | Role |
| --- | --- | --- | --- |
| `GET` | `/patients` | Tìm kiếm/danh sách bệnh nhân | Receptionist/Doctor/Admin |
| `GET` | `/patients/{id}` | Xem chi tiết bệnh nhân | Receptionist/Doctor/Admin |
| `POST` | `/patients` | Tạo hồ sơ bệnh nhân | Receptionist/Admin |
| `PUT` | `/patients/{id}` | Cập nhật hồ sơ bệnh nhân | Receptionist/Admin |

Patient request tối thiểu:

```json
{
  "fullName": "Nguyen Van A",
  "gender": "MALE",
  "dateOfBirth": "2000-01-01",
  "phone": "0900000000",
  "address": "Ho Chi Minh City"
}
```

## 10. Appointment endpoints

| Method | Endpoint | Mục đích | Role |
| --- | --- | --- | --- |
| `GET` | `/appointments` | Danh sách lịch hẹn | Receptionist/Doctor/Admin |
| `GET` | `/appointments/{id}` | Chi tiết lịch hẹn | Receptionist/Doctor/Admin |
| `POST` | `/appointments` | Đặt lịch hẹn | Receptionist/Admin |
| `PATCH` | `/appointments/{id}/check-in` | Check-in bệnh nhân | Receptionist |
| `PATCH` | `/appointments/{id}/cancel` | Hủy lịch hẹn | Receptionist/Admin |
| `PATCH` | `/appointments/{id}/no-show` | Đánh dấu không đến | Receptionist |

Appointment status MVP:

```text
BOOKED -> CHECKED_IN -> IN_CONSULTATION -> COMPLETED
BOOKED -> CANCELLED
BOOKED -> NO_SHOW
```

Duplicate appointment slot được xử lý trước bằng service logic: cùng `doctorId`, `appointmentDate`, `startTime`, `endTime` không được có appointment active trùng nhau.

## 11. Queue endpoints

| Method | Endpoint | Mục đích | Role |
| --- | --- | --- | --- |
| `GET` | `/queue-items` | Xem hàng đợi trong ngày | Receptionist/Doctor/Admin |
| `POST` | `/queue-items` | Thêm bệnh nhân vào hàng đợi | Receptionist |
| `PATCH` | `/queue-items/{id}/call` | Gọi bệnh nhân | Doctor/Receptionist |
| `PATCH` | `/queue-items/{id}/start-service` | Bắt đầu phục vụ | Doctor |
| `PATCH` | `/queue-items/{id}/done` | Hoàn tất hàng đợi | Doctor |
| `PATCH` | `/queue-items/{id}/skip` | Bỏ lượt | Receptionist/Doctor |

Queue status MVP:

```text
WAITING -> CALLED -> IN_SERVICE -> DONE
WAITING/CALLED -> SKIPPED
```

## 12. Visit và encounter endpoints

| Method | Endpoint | Mục đích | Role |
| --- | --- | --- | --- |
| `GET` | `/visits` | Danh sách lượt khám | Doctor/Admin |
| `GET` | `/visits/{id}` | Chi tiết lượt khám | Doctor/Admin |
| `POST` | `/visits` | Tạo lượt khám từ appointment/queue | Receptionist/Doctor |
| `GET` | `/encounters/{id}` | Chi tiết phiếu khám | Doctor |
| `POST` | `/encounters` | Mở phiếu khám | Doctor |
| `PUT` | `/encounters/{id}` | Cập nhật chẩn đoán/ghi chú | Doctor |
| `PATCH` | `/encounters/{id}/complete` | Hoàn tất phiếu khám | Doctor |

Encounter status MVP:

```text
OPEN -> COMPLETED
```

## 13. Service catalog và encounter service endpoints

| Method | Endpoint | Mục đích | Role |
| --- | --- | --- | --- |
| `GET` | `/services` | Danh mục dịch vụ | Doctor/Cashier/Admin |
| `POST` | `/services` | Tạo dịch vụ | Admin |
| `PUT` | `/services/{id}` | Cập nhật dịch vụ | Admin |
| `POST` | `/encounters/{id}/services` | Chỉ định dịch vụ cho phiếu khám | Doctor |
| `PATCH` | `/encounter-services/{id}/complete` | Hoàn tất dịch vụ | Doctor/Admin |
| `PATCH` | `/encounter-services/{id}/cancel` | Hủy dịch vụ | Doctor/Admin |

## 14. Billing và payment endpoints

| Method | Endpoint | Mục đích | Role |
| --- | --- | --- | --- |
| `GET` | `/invoices` | Danh sách hóa đơn | Cashier/Admin |
| `GET` | `/invoices/{id}` | Chi tiết hóa đơn | Cashier/Admin |
| `POST` | `/invoices` | Phát hành hóa đơn | Cashier/Admin |
| `PATCH` | `/invoices/{id}/cancel` | Hủy hóa đơn | Cashier/Admin |
| `POST` | `/invoices/{id}/payments` | Ghi nhận thanh toán | Cashier |
| `GET` | `/payments` | Danh sách thanh toán | Cashier/Admin |

Invoice status MVP:

```text
ISSUED -> PAID
ISSUED -> CANCELLED
```

Payment status MVP:

```text
SUCCESS
FAILED
```

## 15. Report endpoints

| Method | Endpoint | Mục đích | Role |
| --- | --- | --- | --- |
| `GET` | `/reports/daily-revenue` | Doanh thu theo ngày | Admin |
| `GET` | `/reports/appointments` | Thống kê lịch hẹn | Admin |
| `GET` | `/reports/doctor-workload` | Thống kê lượt khám theo bác sĩ | Admin |

## 16. Quy ước cho JavaFX client

Luồng bắt buộc:

```text
FXML Controller -> Desktop Service -> API Client -> Backend REST API
```

- JavaFX không gọi repository/database trực tiếp.
- JavaFX không tự xử lý business rule quan trọng; backend là nguồn quyết định cuối cùng.
- JavaFX đọc `success`, `message`, `data`, `errors` để hiển thị kết quả.
- REST integration JavaFX do Member 1 - Nguyễn Duy Phương phụ trách.

## 17. Quy tắc thay đổi contract

- Thay đổi endpoint, enum, required field hoặc response shape phải cập nhật tài liệu này.
- Không đổi tên field đã được JavaFX dùng nếu chưa thông báo team.
- Với MVP 1 tuần, ưu tiên contract đơn giản, rõ ràng, dễ test hơn là thiết kế quá rộng.
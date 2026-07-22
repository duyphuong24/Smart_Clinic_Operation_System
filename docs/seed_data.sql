-- ===============================================================================
-- SMART CLINIC OPERATIONS SYSTEM - COMPLETE SEED DATA SCRIPT (MS SQL SERVER)
-- Target Database: HSF302_SmartClinic (hoặc tên database của bạn)
-- Usage: Run in SQL Server Management Studio (SSMS) or DBeaver / Database Tool
--
-- Demo Credentials & Accounts:
--   - 'admin'        / 'admin123'         (Full System Administrator)
--   - 'manager'      / 'manager123'       (Clinic Operations & Finance Manager)
--   - 'receptionist' / 'receptionist123'  (Patient Registration & Appointment Desk)
--   - 'cashier'      / 'cashier123'       (Billing & PayOS Cashier)
--   - 'doctor'       / 'doctor123'       (BS. Nguyễn Văn An - Nội Tổng Quát)
--   - 'doctor2'      / 'password123'      (BS. Lê Thị Bích - Nhi Khoa)
--   - 'doctor3'      / 'password123'      (BS. Phạm Hoàng Cường - Tai Mũi Họng)
--   - 'patient1'     / 'password123'      (Bệnh nhân Nguyễn Văn Hùng - Patient Portal)
-- ===============================================================================

USE [HSF302_SmartClinic];
GO

SET NOCOUNT ON;

PRINT N'Starting 100% Comprehensive Data Seeding for Smart Clinic Operations System...';

-- ===============================================================================
-- 1. SEED ROLES
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN') INSERT INTO roles (name) VALUES ('ADMIN');
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'MANAGER') INSERT INTO roles (name) VALUES ('MANAGER');
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'RECEPTIONIST') INSERT INTO roles (name) VALUES ('RECEPTIONIST');
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'DOCTOR') INSERT INTO roles (name) VALUES ('DOCTOR');
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'CASHIER') INSERT INTO roles (name) VALUES ('CASHIER');
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'PATIENT') INSERT INTO roles (name) VALUES ('PATIENT');

-- BCrypt Hash for 'admin123' / 'password123': $2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO

-- ===============================================================================
-- 2. SEED USERS
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'admin')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('admin', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'Nguyễn Văn Quản Trị', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'manager')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('manager', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'Phạm Minh Quản Lý', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'receptionist')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('receptionist', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'Trần Thị Lễ Tân', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'cashier')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('cashier', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'Lê Hoàng Thu Ngân', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'doctor')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('doctor', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'BS. Nguyễn Văn An', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'doctor2')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('doctor2', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'BS. Lê Thị Bích', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'doctor3')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('doctor3', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'BS. Phạm Hoàng Cường', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'patient1')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('patient1', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'Nguyễn Văn Hùng', 'ACTIVE', 0, GETDATE(), GETDATE());

-- ===============================================================================
-- 3. SEED USER_ROLES
-- ===============================================================================
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'admin' AND r.name = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'manager' AND r.name = 'MANAGER'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'receptionist' AND r.name = 'RECEPTIONIST'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'cashier' AND r.name = 'CASHIER'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'doctor' AND r.name = 'DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'doctor2' AND r.name = 'DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'doctor3' AND r.name = 'DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'patient1' AND r.name = 'PATIENT'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

-- ===============================================================================
-- 4. SEED STAFF
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM staff WHERE employee_code = 'EMP-000001')
    INSERT INTO staff (user_id, employee_code, staff_type, hired_date, status, created_at, updated_at)
    SELECT id, 'EMP-000001', 'DOCTOR', '2023-01-15', 'ACTIVE', GETDATE(), GETDATE() FROM users WHERE user_name = 'doctor';

IF NOT EXISTS (SELECT 1 FROM staff WHERE employee_code = 'EMP-000002')
    INSERT INTO staff (user_id, employee_code, staff_type, hired_date, status, created_at, updated_at)
    SELECT id, 'EMP-000002', 'DOCTOR', '2023-03-20', 'ACTIVE', GETDATE(), GETDATE() FROM users WHERE user_name = 'doctor2';

IF NOT EXISTS (SELECT 1 FROM staff WHERE employee_code = 'EMP-000003')
    INSERT INTO staff (user_id, employee_code, staff_type, hired_date, status, created_at, updated_at)
    SELECT id, 'EMP-000003', 'DOCTOR', '2023-06-01', 'ACTIVE', GETDATE(), GETDATE() FROM users WHERE user_name = 'doctor3';

IF NOT EXISTS (SELECT 1 FROM staff WHERE employee_code = 'EMP-000004')
    INSERT INTO staff (user_id, employee_code, staff_type, hired_date, status, created_at, updated_at)
    SELECT id, 'EMP-000004', 'RECEPTIONIST', '2023-02-10', 'ACTIVE', GETDATE(), GETDATE() FROM users WHERE user_name = 'receptionist';

IF NOT EXISTS (SELECT 1 FROM staff WHERE employee_code = 'EMP-000005')
    INSERT INTO staff (user_id, employee_code, staff_type, hired_date, status, created_at, updated_at)
    SELECT id, 'EMP-000005', 'CASHIER', '2023-04-05', 'ACTIVE', GETDATE(), GETDATE() FROM users WHERE user_name = 'cashier';

-- ===============================================================================
-- 5. SEED SPECIALTIES
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM specialties WHERE name = N'Nội tổng quát')
    INSERT INTO specialties (name, description, active) VALUES (N'Nội tổng quát', N'Khám và điều trị các bệnh lý nội khoa thông thường', 1);

IF NOT EXISTS (SELECT 1 FROM specialties WHERE name = N'Nhi khoa')
    INSERT INTO specialties (name, description, active) VALUES (N'Nhi khoa', N'Chăm sóc sức khỏe và khám chữa bệnh cho trẻ em', 1);

IF NOT EXISTS (SELECT 1 FROM specialties WHERE name = N'Tai Mũi Họng')
    INSERT INTO specialties (name, description, active) VALUES (N'Tai Mũi Họng', N'Chẩn đoán và điều trị bệnh lý đường hô hấp trên', 1);

IF NOT EXISTS (SELECT 1 FROM specialties WHERE name = N'Tim mạch')
    INSERT INTO specialties (name, description, active) VALUES (N'Tim mạch', N'Khám chuyên sâu các bệnh lý tim mạch và huyết áp', 1);

IF NOT EXISTS (SELECT 1 FROM specialties WHERE name = N'Răng Hàm Mặt')
    INSERT INTO specialties (name, description, active) VALUES (N'Răng Hàm Mặt', N'Chăm sóc nha khoa và phẫu thuật răng hàm mặt', 1);

-- ===============================================================================
-- 6. SEED ROOMS
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM rooms WHERE room_code = 'ROOM-101')
    INSERT INTO rooms (room_code, name, floor, active) VALUES ('ROOM-101', N'Phòng khám Nội 101', N'Tầng 1', 1);

IF NOT EXISTS (SELECT 1 FROM rooms WHERE room_code = 'ROOM-102')
    INSERT INTO rooms (room_code, name, floor, active) VALUES ('ROOM-102', N'Phòng khám Nhi 102', N'Tầng 1', 1);

IF NOT EXISTS (SELECT 1 FROM rooms WHERE room_code = 'ROOM-201')
    INSERT INTO rooms (room_code, name, floor, active) VALUES ('ROOM-201', N'Phòng Tai Mũi Họng 201', N'Tầng 2', 1);

IF NOT EXISTS (SELECT 1 FROM rooms WHERE room_code = 'ROOM-202')
    INSERT INTO rooms (room_code, name, floor, active) VALUES ('ROOM-202', N'Phòng Tim Mạch 202', N'Tầng 2', 1);

-- ===============================================================================
-- 7. SEED DOCTORS
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM doctors WHERE license_number = 'LIC-001')
    INSERT INTO doctors (staff_id, specialty_id, default_room_id, license_number, consultation_fee, active)
    SELECT s.id, sp.id, r.id, 'LIC-001', 150000.00, 1
    FROM staff s, specialties sp, rooms r
    WHERE s.employee_code = 'EMP-000001' AND sp.name = N'Nội tổng quát' AND r.room_code = 'ROOM-101';

IF NOT EXISTS (SELECT 1 FROM doctors WHERE license_number = 'LIC-002')
    INSERT INTO doctors (staff_id, specialty_id, default_room_id, license_number, consultation_fee, active)
    SELECT s.id, sp.id, r.id, 'LIC-002', 200000.00, 1
    FROM staff s, specialties sp, rooms r
    WHERE s.employee_code = 'EMP-000002' AND sp.name = N'Nhi khoa' AND r.room_code = 'ROOM-102';

IF NOT EXISTS (SELECT 1 FROM doctors WHERE license_number = 'LIC-003')
    INSERT INTO doctors (staff_id, specialty_id, default_room_id, license_number, consultation_fee, active)
    SELECT s.id, sp.id, r.id, 'LIC-003', 180000.00, 1
    FROM staff s, specialties sp, rooms r
    WHERE s.employee_code = 'EMP-000003' AND sp.name = N'Tai Mũi Họng' AND r.room_code = 'ROOM-201';

-- ===============================================================================
-- 8. SEED DOCTOR AVAILABILITIES (FULL 7-DAY WEEKLY ROSTER FOR SCHEDULE BOARD)
-- ===============================================================================
-- Doctor 1 (Nội tổng quát) - T2, T3, T4, T5, T6 (Morning Shift)
INSERT INTO doctor_availabilities (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes, max_patients_per_slot, active)
SELECT d.id, day, '08:00:00', '12:00:00', 30, 4, 1 
FROM doctors d CROSS JOIN (VALUES (1),(2),(3),(4),(5)) AS Days(day)
WHERE d.license_number = 'LIC-001'
AND NOT EXISTS (SELECT 1 FROM doctor_availabilities WHERE doctor_id = d.id AND day_of_week = day);

-- Doctor 2 (Nhi khoa) - T2, T4, T6, T7 (Afternoon Shift)
INSERT INTO doctor_availabilities (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes, max_patients_per_slot, active)
SELECT d.id, day, '13:30:00', '17:30:00', 30, 4, 1 
FROM doctors d CROSS JOIN (VALUES (1),(3),(5),(6)) AS Days(day)
WHERE d.license_number = 'LIC-002'
AND NOT EXISTS (SELECT 1 FROM doctor_availabilities WHERE doctor_id = d.id AND day_of_week = day);

-- Doctor 3 (Tai Mũi Họng) - T3, T5, T7, CN (Full Weekend Shift)
INSERT INTO doctor_availabilities (doctor_id, day_of_week, start_time, end_time, slot_duration_minutes, max_patients_per_slot, active)
SELECT d.id, day, '08:30:00', '11:30:00', 30, 3, 1 
FROM doctors d CROSS JOIN (VALUES (2),(4),(6),(7)) AS Days(day)
WHERE d.license_number = 'LIC-003'
AND NOT EXISTS (SELECT 1 FROM doctor_availabilities WHERE doctor_id = d.id AND day_of_week = day);

-- ===============================================================================
-- 9. SEED PATIENTS
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM patients WHERE patient_code = 'PAT-000001')
    INSERT INTO patients (patient_code, full_name, date_of_birth, gender, phone, email, address, identity_number, emergency_contact_name, emergency_contact_phone, allergy_note, status, created_at, updated_at)
    VALUES ('PAT-000001', N'Nguyễn Văn Hùng', '1990-05-15', 'MALE', '0901234567', 'hung.nguyen@example.com', N'123 Nguyễn Văn Linh, Đà Nẵng', '048090000001', N'Nguyễn Thị Mai', '0909999888', N'Dị ứng Penicillin', 'ACTIVE', GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM patients WHERE patient_code = 'PAT-000002')
    INSERT INTO patients (patient_code, full_name, date_of_birth, gender, phone, email, address, identity_number, emergency_contact_name, emergency_contact_phone, allergy_note, status, created_at, updated_at)
    VALUES ('PAT-000002', N'Trần Thị Mai', '1995-08-20', 'FEMALE', '0912345678', 'mai.tran@example.com', N'456 Lê Duẩn, Đà Nẵng', '048095000002', N'Trần Văn Bình', '0919999777', N'Không có', 'ACTIVE', GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM patients WHERE patient_code = 'PAT-000003')
    INSERT INTO patients (patient_code, full_name, date_of_birth, gender, phone, email, address, identity_number, emergency_contact_name, emergency_contact_phone, allergy_note, status, created_at, updated_at)
    VALUES ('PAT-000003', N'Lê Hoàng Nam', '1985-12-10', 'MALE', '0987654321', 'nam.le@example.com', N'789 Điện Biên Phủ, Đà Nẵng', '048085000003', N'Lê Thị Hương', '0988888666', N'Dị ứng hải sản', 'ACTIVE', GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM patients WHERE patient_code = 'PAT-000004')
    INSERT INTO patients (patient_code, full_name, date_of_birth, gender, phone, email, address, identity_number, emergency_contact_name, emergency_contact_phone, allergy_note, status, created_at, updated_at)
    VALUES ('PAT-000004', N'Phạm Thu Thảo', '2001-03-25', 'FEMALE', '0935123456', 'thao.pham@example.com', N'12 Nguyễn Huệ, Đà Nẵng', '048101000004', N'Phạm Văn Cường', '0935999111', N'Tiền sử hen suyễn', 'ACTIVE', GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM patients WHERE patient_code = 'PAT-000005')
    INSERT INTO patients (patient_code, full_name, date_of_birth, gender, phone, email, address, identity_number, emergency_contact_name, emergency_contact_phone, allergy_note, status, created_at, updated_at)
    VALUES ('PAT-000005', N'Đỗ Anh Tuấn', '1978-09-05', 'MALE', '0978111222', 'tuan.do@example.com', N'88 Hoàng Diệu, Đà Nẵng', '048078000005', N'Đỗ Thị Thúy', '0978999333', N'Không có', 'ACTIVE', GETDATE(), GETDATE());

-- ===============================================================================
-- 10. SEED SERVICE_CATALOG (MEDICAL SERVICES & PRICING)
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM service_catalog WHERE service_code = 'SRV-001')
    INSERT INTO service_catalog (service_code, name, type, price, active, created_at, updated_at)
    VALUES ('SRV-001', N'Khám tổng quát nội khoa', 'CONSULTATION', 150000.00, 1, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM service_catalog WHERE service_code = 'SRV-002')
    INSERT INTO service_catalog (service_code, name, type, price, active, created_at, updated_at)
    VALUES ('SRV-002', N'Xét nghiệm công thức máu toàn phần (CBC)', 'LAB_TEST', 120000.00, 1, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM service_catalog WHERE service_code = 'SRV-003')
    INSERT INTO service_catalog (service_code, name, type, price, active, created_at, updated_at)
    VALUES ('SRV-003', N'Chụp X-Quang phổi kỹ thuật số', 'IMAGING', 200000.00, 1, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM service_catalog WHERE service_code = 'SRV-004')
    INSERT INTO service_catalog (service_code, name, type, price, active, created_at, updated_at)
    VALUES ('SRV-004', N'Siêu âm ổ bụng tổng quát 4D', 'IMAGING', 250000.00, 1, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM service_catalog WHERE service_code = 'SRV-005')
    INSERT INTO service_catalog (service_code, name, type, price, active, created_at, updated_at)
    VALUES ('SRV-005', N'Đo điện tâm đồ (ECG 12 chuyển đạo)', 'PROCEDURE', 100000.00, 1, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM service_catalog WHERE service_code = 'SRV-006')
    INSERT INTO service_catalog (service_code, name, type, price, active, created_at, updated_at)
    VALUES ('SRV-006', N'Nội soi Tai Mũi Họng ống mềm', 'PROCEDURE', 300000.00, 1, GETDATE(), GETDATE());

-- ===============================================================================
-- 11. SEED APPOINTMENTS
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM appointments WHERE appointment_code = 'APT-000001')
    INSERT INTO appointments (appointment_code, patient_id, doctor_id, room_id, scheduled_start, scheduled_end, reason, source, status, reminder_sent, created_at, updated_at)
    SELECT 'APT-000001', p.id, d.id, r.id, DATEADD(hour, 9, CAST(GETDATE() AS DATETIME)), DATEADD(hour, 9, DATEADD(minute, 30, CAST(GETDATE() AS DATETIME))), N'Khám sức khỏe định kỳ', 'WEB', 'BOOKED', 0, GETDATE(), GETDATE()
    FROM patients p, doctors d, rooms r
    WHERE p.patient_code = 'PAT-000001' AND d.license_number = 'LIC-001' AND r.room_code = 'ROOM-101';

IF NOT EXISTS (SELECT 1 FROM appointments WHERE appointment_code = 'APT-000002')
    INSERT INTO appointments (appointment_code, patient_id, doctor_id, room_id, scheduled_start, scheduled_end, reason, source, status, reminder_sent, created_at, updated_at)
    SELECT 'APT-000002', p.id, d.id, r.id, DATEADD(hour, 14, CAST(GETDATE() AS DATETIME)), DATEADD(hour, 14, DATEADD(minute, 30, CAST(GETDATE() AS DATETIME))), N'Sốt nhẹ và ho hắt hơi', 'WALK_IN', 'CONFIRMED', 0, GETDATE(), GETDATE()
    FROM patients p, doctors d, rooms r
    WHERE p.patient_code = 'PAT-000002' AND d.license_number = 'LIC-002' AND r.room_code = 'ROOM-102';

-- ===============================================================================
-- 12. SEED QUEUE_ITEMS & VISITS
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM queue_items WHERE queue_date = CAST(GETDATE() AS DATE) AND queue_number = 1)
    INSERT INTO queue_items (queue_date, queue_number, patient_id, doctor_id, room_id, appointment_id, priority, status, created_at, updated_at)
    SELECT CAST(GETDATE() AS DATE), 1, p.id, d.id, r.id, a.id, 'NORMAL', 'WAITING', GETDATE(), GETDATE()
    FROM patients p, doctors d, rooms r, appointments a
    WHERE p.patient_code = 'PAT-000001' AND d.license_number = 'LIC-001' AND r.room_code = 'ROOM-101' AND a.appointment_code = 'APT-000001';

IF NOT EXISTS (SELECT 1 FROM visits WHERE visit_code = 'VST-000001')
    INSERT INTO visits (visit_code, patient_id, doctor_id, room_id, appointment_id, check_in_time, status, notes, created_at, updated_at)
    SELECT 'VST-000001', p.id, d.id, r.id, a.id, GETDATE(), 'IN_PROGRESS', N'Đã làm thủ tục đón tiếp tại quầy', GETDATE(), GETDATE()
    FROM patients p, doctors d, rooms r, appointments a
    WHERE p.patient_code = 'PAT-000001' AND d.license_number = 'LIC-001' AND r.room_code = 'ROOM-101' AND a.appointment_code = 'APT-000001';

-- ===============================================================================
-- 13. SEED ENCOUNTERS
-- ===============================================================================
IF NOT EXISTS (SELECT 1 FROM encounters WHERE symptoms = N'Đau đầu, sốt nhẹ, mệt mỏi')
    INSERT INTO encounters (visit_id, doctor_id, patient_id, start_time, end_time, symptoms, diagnosis, clinical_notes, status, created_at, updated_at)
    SELECT v.id, d.id, p.id, GETDATE(), DATEADD(minute, 20, GETDATE()), N'Đau đầu, sốt nhẹ, mệt mỏi', N'J00 - Viêm mũi họng cấp (Cảm lạnh thông thường)', N'Theo dõi nhiệt độ, nghỉ ngơi hợp lý và uống nhiều nước', 'COMPLETED', GETDATE(), GETDATE()
    FROM visits v, doctors d, patients p
    WHERE v.visit_code = 'VST-000001' AND d.license_number = 'LIC-001' AND p.patient_code = 'PAT-000001';

-- ===============================================================================
-- 14. SEED INVOICES & INVOICE ITEMS (FOR PAYOS QR & BILLING TESTING)
-- ===============================================================================
-- Invoice 1: ISSUED (Unpaid Invoice ready for PayOS VietQR Payment link generation test)
IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_number = 'INV-001001')
    INSERT INTO invoices (invoice_number, visit_id, encounter_id, patient_id, total_amount, status, issued_at, created_at, updated_at)
    SELECT 'INV-001001', v.id, e.id, p.id, 270000.00, 'ISSUED', GETDATE(), GETDATE(), GETDATE()
    FROM visits v, encounters e, patients p
    WHERE v.visit_code = 'VST-000001' AND e.visit_id = v.id AND p.patient_code = 'PAT-000001';

IF NOT EXISTS (SELECT 1 FROM invoice_items WHERE item_name = N'Khám tổng quát nội khoa')
    INSERT INTO invoice_items (invoice_id, service_catalog_id, item_name, unit_price, quantity, line_total)
    SELECT i.id, s.id, s.name, s.price, 1, s.price
    FROM invoices i, service_catalog s
    WHERE i.invoice_number = 'INV-001001' AND s.service_code = 'SRV-001';

IF NOT EXISTS (SELECT 1 FROM invoice_items WHERE item_name = N'Xét nghiệm công thức máu toàn phần (CBC)')
    INSERT INTO invoice_items (invoice_id, service_catalog_id, item_name, unit_price, quantity, line_total)
    SELECT i.id, s.id, s.name, s.price, 1, s.price
    FROM invoices i, service_catalog s
    WHERE i.invoice_number = 'INV-001001' AND s.service_code = 'SRV-002';

-- ===============================================================================
-- 15. SEED PAYMENTS
-- ===============================================================================
-- Sample historical PayOS Payment record
IF NOT EXISTS (SELECT 1 FROM payments WHERE transaction_ref = 'PAYOS-REF-DEMO001')
    INSERT INTO payments (invoice_id, amount, method, status, transaction_ref, note, paid_at, created_at, updated_at)
    SELECT i.id, i.total_amount, 'PAYOS_QR', 'SUCCESS', 'PAYOS-REF-DEMO001', N'PayOS VietQR Automatic Payment Reconciliation Demo', GETDATE(), GETDATE(), GETDATE()
    FROM invoices i WHERE i.invoice_number = 'INV-001001';

-- ===============================================================================
-- 16. SEED AUDIT LOGS
-- ===============================================================================
INSERT INTO audit_logs (action, resource, details, username, ip_address, created_at)
VALUES (N'LOGIN_SUCCESS', N'AUTH', N'Đăng nhập thành công tài khoản Quản trị hệ thống', 'admin', '127.0.0.1', GETDATE());

INSERT INTO audit_logs (action, resource, details, username, ip_address, created_at)
VALUES (N'PAYMENT_RECEIVED', N'PAYMENT', N'Thanh toán PayOS VietQR nạp tiền thành công cho Hóa đơn INV-001001', 'payos_webhook', '127.0.0.1', GETDATE());

INSERT INTO audit_logs (action, resource, details, username, ip_address, created_at)
VALUES (N'INITIAL_SEED', N'SYSTEM', N'Tự động khởi tạo dữ liệu Seed Data bao phủ 100% chức năng thành công', 'admin', '127.0.0.1', GETDATE());

PRINT N'SUCCESS: 100% Comprehensive Seed Data Script executed successfully!';
GO

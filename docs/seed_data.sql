-- ===============================================================================
-- SMART CLINIC OPERATION SYSTEM - FAKE SEED DATA SCRIPT (MS SQL SERVER)
-- Database: HSF302_SmartClinic
-- Usage: Run in SQL Server Management Studio (SSMS) or IntelliJ Database Tool
-- Default Passwords for all seeded users: password123
-- ===============================================================================

USE [HSF302_SmartClinic];
GO

SET NOCOUNT ON;

PRINT N'Starting Data Seeding for Smart Clinic...';

-- 1. SEED ROLES
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN')
    INSERT INTO roles (name) VALUES ('ADMIN');
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'RECEPTIONIST')
    INSERT INTO roles (name) VALUES ('RECEPTIONIST');
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'DOCTOR')
    INSERT INTO roles (name) VALUES ('DOCTOR');
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'CASHIER')
    INSERT INTO roles (name) VALUES ('CASHIER');
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'MANAGER')
    INSERT INTO roles (name) VALUES ('MANAGER');

-- BCrypt Hash for 'password123': $2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO

-- 2. SEED USERS
IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'admin')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('admin', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'Nguyễn Văn Quản Trị', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'receptionist1')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('receptionist1', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'Trần Thị Lễ Tân', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'doctor1')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('doctor1', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'BS. Lê Hoàng Nam', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'doctor2')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('doctor2', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'BS. Phạm Minh Anh', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'cashier1')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('cashier1', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'Vũ Thu Ngân', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'manager1')
    INSERT INTO users (user_name, password_hash, full_name, status, failed_login_attempts, created_at, updated_at)
    VALUES ('manager1', '$2a$10$AFYDDESyaKD8BKOXQJSbUOyTHkr/u/meuGNrIMwoWvN1qeCLmE9SO', N'Đỗ Quản Lý', 'ACTIVE', 0, GETDATE(), GETDATE());

-- 3. MAP USER ROLES
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'admin' AND r.name = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'receptionist1' AND r.name = 'RECEPTIONIST'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'doctor1' AND r.name = 'DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'doctor2' AND r.name = 'DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'cashier1' AND r.name = 'CASHIER'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'manager1' AND r.name = 'MANAGER'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

-- 4. SEED ROOMS
IF NOT EXISTS (SELECT 1 FROM rooms WHERE room_code = 'ROOM-101')
    INSERT INTO rooms (room_code, name, floor, active) VALUES ('ROOM-101', N'Phòng Khám Nội 101', N'Tầng 1', 1);
IF NOT EXISTS (SELECT 1 FROM rooms WHERE room_code = 'ROOM-102')
    INSERT INTO rooms (room_code, name, floor, active) VALUES ('ROOM-102', N'Phòng Khám Tim Mạch 102', N'Tầng 1', 1);
IF NOT EXISTS (SELECT 1 FROM rooms WHERE room_code = 'ROOM-201')
    INSERT INTO rooms (room_code, name, floor, active) VALUES ('ROOM-201', N'Phòng Xét Nghiệm 201', N'Tầng 2', 1);

-- 5. SEED SPECIALTIES
IF NOT EXISTS (SELECT 1 FROM specialties WHERE name = N'Nội Tổng Quát')
    INSERT INTO specialties (name, description, active) VALUES (N'Nội Tổng Quát', N'Khám và điều trị các bệnh lý nội khoa chung', 1);
IF NOT EXISTS (SELECT 1 FROM specialties WHERE name = N'Tim Mạch')
    INSERT INTO specialties (name, description, active) VALUES (N'Tim Mạch', N'Khám và chẩn đoán bệnh lý tim mạch, huyết áp', 1);

-- 6. SEED SERVICE CATALOG
IF NOT EXISTS (SELECT 1 FROM service_catalog WHERE service_code = 'SVC-CONS')
    INSERT INTO service_catalog (service_code, name, type, price, active, version, created_at, updated_at) VALUES ('SVC-CONS', N'Tiền Khám Khám Bệnh', 'CONSULTATION', 150000.00, 1, 0, GETDATE(), GETDATE());
IF NOT EXISTS (SELECT 1 FROM service_catalog WHERE service_code = 'SVC-CBC')
    INSERT INTO service_catalog (service_code, name, type, price, active, version, created_at, updated_at) VALUES ('SVC-CBC', N'Xét Nghiệm Công Thức Máu (CBC)', 'LAB_TEST', 120000.00, 1, 0, GETDATE(), GETDATE());
IF NOT EXISTS (SELECT 1 FROM service_catalog WHERE service_code = 'SVC-XPUL')
    INSERT INTO service_catalog (service_code, name, type, price, active, version, created_at, updated_at) VALUES ('SVC-XPUL', N'Chụp X-Quang Phổi Thẳng', 'PROCEDURE', 250000.00, 1, 0, GETDATE(), GETDATE());

-- 7. SEED STAFF
IF NOT EXISTS (SELECT 1 FROM staff WHERE employee_code = 'EMP-001')
    INSERT INTO staff (user_id, employee_code, staff_type, status, hired_date, version, created_at, updated_at)
    SELECT id, 'EMP-001', 'MANAGER', 'ACTIVE', '2025-01-01', 0, GETDATE(), GETDATE() FROM users WHERE user_name = 'admin';

IF NOT EXISTS (SELECT 1 FROM staff WHERE employee_code = 'EMP-002')
    INSERT INTO staff (user_id, employee_code, staff_type, status, hired_date, version, created_at, updated_at)
    SELECT id, 'EMP-002', 'RECEPTIONIST', 'ACTIVE', '2025-01-15', 0, GETDATE(), GETDATE() FROM users WHERE user_name = 'receptionist1';

IF NOT EXISTS (SELECT 1 FROM staff WHERE employee_code = 'EMP-003')
    INSERT INTO staff (user_id, employee_code, staff_type, status, hired_date, version, created_at, updated_at)
    SELECT id, 'EMP-003', 'DOCTOR', 'ACTIVE', '2025-02-01', 0, GETDATE(), GETDATE() FROM users WHERE user_name = 'doctor1';

IF NOT EXISTS (SELECT 1 FROM staff WHERE employee_code = 'EMP-004')
    INSERT INTO staff (user_id, employee_code, staff_type, status, hired_date, version, created_at, updated_at)
    SELECT id, 'EMP-004', 'DOCTOR', 'ACTIVE', '2025-02-01', 0, GETDATE(), GETDATE() FROM users WHERE user_name = 'doctor2';

IF NOT EXISTS (SELECT 1 FROM staff WHERE employee_code = 'EMP-005')
    INSERT INTO staff (user_id, employee_code, staff_type, status, hired_date, version, created_at, updated_at)
    SELECT id, 'EMP-005', 'CASHIER', 'ACTIVE', '2025-02-10', 0, GETDATE(), GETDATE() FROM users WHERE user_name = 'cashier1';

-- 8. SEED DOCTORS
IF NOT EXISTS (SELECT 1 FROM doctors WHERE license_no = 'LIC-10001')
    INSERT INTO doctors (staff_id, specialty_id, default_room_id, license_no, consultation_fee, bio, active, version, created_at, updated_at)
    SELECT s.id, sp.id, rm.id, 'LIC-10001', 150000.00, N'Bác sĩ Chuyên khoa I Nội Tổng Quát hơn 10 năm kinh nghiệm.', 1, 0, GETDATE(), GETDATE()
    FROM staff s, specialties sp, rooms rm
    WHERE s.employee_code = 'EMP-003' AND sp.name = N'Nội Tổng Quát' AND rm.room_code = 'ROOM-101';

IF NOT EXISTS (SELECT 1 FROM doctors WHERE license_no = 'LIC-10002')
    INSERT INTO doctors (staff_id, specialty_id, default_room_id, license_no, consultation_fee, bio, active, version, created_at, updated_at)
    SELECT s.id, sp.id, rm.id, 'LIC-10002', 200000.00, N'Bác sĩ Chuyên khoa Tim Mạch Bệnh viện Trung Vương.', 1, 0, GETDATE(), GETDATE()
    FROM staff s, specialties sp, rooms rm
    WHERE s.employee_code = 'EMP-004' AND sp.name = N'Tim Mạch' AND rm.room_code = 'ROOM-102';

-- 9. SEED PATIENTS
IF NOT EXISTS (SELECT 1 FROM patients WHERE patient_code = 'BN-20260701-0001')
    INSERT INTO patients (patient_code, full_name, phone, date_of_birth, gender, address, status, version, created_at, updated_at)
    VALUES ('BN-20260701-0001', N'Nguyễn Văn An', '0905111222', '1990-05-15', 'MALE', N'123 Nguyễn Văn Linh, Đà Nẵng', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM patients WHERE patient_code = 'BN-20260701-0002')
    INSERT INTO patients (patient_code, full_name, phone, date_of_birth, gender, address, status, version, created_at, updated_at)
    VALUES ('BN-20260701-0002', N'Trần Thị Bình', '0914333444', '1985-08-20', 'FEMALE', N'456 Lê Duẩn, Đà Nẵng', 'ACTIVE', 0, GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM patients WHERE patient_code = 'BN-20260701-0003')
    INSERT INTO patients (patient_code, full_name, phone, date_of_birth, gender, address, status, version, created_at, updated_at)
    VALUES ('BN-20260701-0003', N'Lê Hoàng Cường', '0988555666', '1995-12-10', 'MALE', N'789 Điện Biên Phủ, Đà Nẵng', 'ACTIVE', 0, GETDATE(), GETDATE());

PRINT N'Successfully seeded fake data for Smart Clinic System!';
GO

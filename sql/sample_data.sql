-- Smart Clinic Management System (Hospital CMS) - Sample Data Script
CREATE DATABASE IF NOT EXISTS cms;
USE cms;

-- Schema definitions (aligned with JPA entities)
CREATE TABLE IF NOT EXISTS admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS doctor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS doctor_available_times (
    doctor_id BIGINT NOT NULL,
    available_times VARCHAR(50) NOT NULL,
    CONSTRAINT fk_doctor_available_times_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS patient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_time DATETIME NOT NULL,
    status INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE
);

-- Seed Admins
INSERT IGNORE INTO admin (id, username, password) VALUES
(1, 'admin', 'admin123'),
(2, 'superadmin', 'super123');

-- Seed Doctors
INSERT IGNORE INTO doctor (id, name, specialty, email, phone, password) VALUES
(1, 'Dr. Emily Carter', 'Cardiology', 'emily.carter@hospitalcms.com', '555-0101', 'doctor123'),
(2, 'Dr. Marcus Vance', 'Neurology', 'marcus.vance@hospitalcms.com', '555-0102', 'doctor123'),
(3, 'Dr. Sofia Patel', 'Pediatrics', 'sofia.patel@hospitalcms.com', '555-0103', 'doctor123'),
(4, 'Dr. Liam OConnor', 'Orthopedics', 'liam.oconnor@hospitalcms.com', '555-0104', 'doctor123'),
(5, 'Dr. Hannah Kim', 'Dermatology', 'hannah.kim@hospitalcms.com', '555-0105', 'doctor123');

-- Seed Doctor Available Times (AM & PM slots)
DELETE FROM doctor_available_times WHERE doctor_id IN (1, 2, 3, 4, 5);
INSERT INTO doctor_available_times (doctor_id, available_times) VALUES
(1, '09:00 AM - 10:00 AM'),
(1, '10:00 AM - 11:00 AM'),
(1, '02:00 PM - 03:00 PM'),
(2, '08:00 AM - 09:00 AM'),
(2, '01:00 PM - 02:00 PM'),
(2, '03:00 PM - 04:00 PM'),
(3, '09:00 AM - 10:00 AM'),
(3, '11:00 AM - 12:00 PM'),
(3, '04:00 PM - 05:00 PM'),
(4, '10:00 AM - 11:00 AM'),
(4, '02:00 PM - 03:00 PM'),
(5, '09:00 AM - 10:00 AM'),
(5, '01:00 PM - 02:00 PM');

-- Seed Patients
INSERT IGNORE INTO patient (id, name, email, password, phone, address) VALUES
(1, 'Alice Johnson', 'alice.johnson@example.com', 'patient123', '555-1001', '123 Maple Street, Springfield'),
(2, 'Bob Williams', 'bob.williams@example.com', 'patient123', '555-1002', '456 Oak Avenue, Springfield'),
(3, 'Clara Martinez', 'clara.martinez@example.com', 'patient123', '555-1003', '789 Pine Road, Riverdale'),
(4, 'David Chen', 'david.chen@example.com', 'patient123', '555-1004', '321 Cedar Lane, Riverdale'),
(5, 'Eva Brooks', 'eva.brooks@example.com', 'patient123', '555-1005', '654 Birch Blvd, Oakwood'),
(6, 'Frank Miller', 'frank.miller@example.com', 'patient123', '555-1006', '987 Elm Court, Oakwood');

-- Seed Appointments across multiple dates, months, and years
-- Status codes: 0 = Scheduled, 1 = Completed, 2 = Cancelled
INSERT IGNORE INTO appointment (id, doctor_id, patient_id, appointment_time, status) VALUES
(1, 1, 1, '2025-10-15 09:00:00', 1),
(2, 1, 2, '2025-10-15 10:00:00', 1),
(3, 1, 3, '2025-10-20 14:00:00', 1),
(4, 2, 4, '2025-10-15 08:00:00', 1),
(5, 3, 5, '2025-11-05 09:00:00', 1),
(6, 1, 1, '2026-03-10 09:00:00', 1),
(7, 1, 4, '2026-03-10 10:00:00', 1),
(8, 2, 2, '2026-03-10 13:00:00', 1),
(9, 3, 3, '2026-03-12 11:00:00', 1),
(10, 3, 5, '2026-03-18 16:00:00', 1),
(11, 3, 6, '2026-03-22 09:00:00', 1),
(12, 1, 6, '2026-11-26 09:00:00', 0),
(13, 2, 1, '2026-11-26 13:00:00', 0),
(14, 4, 2, '2026-11-26 10:00:00', 0),
(15, 5, 3, '2026-11-27 09:00:00', 0);

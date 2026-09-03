-- ============================================================================
-- Sunrise Dental Clinic - Appointment & Patient Management System
-- Database Schema for MySQL 8.x (build/run this in MySQL Workbench)
-- Running this file wipes and rebuilds the database from scratch.
-- If you already have data you want to keep, use migration_v3.sql instead
-- (or migration_v2.sql -> migration_v3.sql in sequence if upgrading from v1).
-- ============================================================================

DROP DATABASE IF EXISTS sunrise_dental;
CREATE DATABASE sunrise_dental CHARACTER SET utf8mb4;
USE sunrise_dental;

-- ----------------------------------------------------------------------------
-- STAFF / LOGIN
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    user_id      INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,   -- SHA-256 hash, see PasswordUtil.java
    full_name    VARCHAR(100) NOT NULL,
    birth_year   INT NULL,
    gender       ENUM('MALE','FEMALE','OTHER') NULL,
    role         ENUM('ADMIN','RECEPTIONIST') NOT NULL DEFAULT 'RECEPTIONIST',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- DENTISTS  (consultation_fee lives here now, not on treatments - it's the
-- dentist's own consultation charge, which can differ doctor to doctor)
-- ----------------------------------------------------------------------------
CREATE TABLE dentists (
    dentist_id       INT AUTO_INCREMENT PRIMARY KEY,
    dentist_name     VARCHAR(100) NOT NULL,
    specialization   VARCHAR(100) DEFAULT 'General Dentistry',
    consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 1000.00,
    active           BOOLEAN DEFAULT TRUE
);

-- ----------------------------------------------------------------------------
-- TREATMENTS (price + duration, used for billing and scheduling/overlap checks)
-- ----------------------------------------------------------------------------
CREATE TABLE treatments (
    treatment_id     INT AUTO_INCREMENT PRIMARY KEY,
    treatment_name   VARCHAR(100) NOT NULL,
    base_fee         DECIMAL(10,2) NOT NULL,
    duration_minutes INT NOT NULL DEFAULT 30
);

-- ----------------------------------------------------------------------------
-- PATIENTS
-- ----------------------------------------------------------------------------
CREATE TABLE patients (
    patient_id     INT AUTO_INCREMENT PRIMARY KEY,
    patient_name   VARCHAR(100) NOT NULL,
    address        VARCHAR(255),
    contact_number VARCHAR(20) NOT NULL,
    birth_year     INT NULL,
    gender         ENUM('MALE','FEMALE','OTHER') NULL,
    registered_on  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_patients_name ON patients(patient_name);
CREATE INDEX idx_patients_contact ON patients(contact_number);

-- ----------------------------------------------------------------------------
-- APPOINTMENTS  (appointment_number is the unique business key shown to staff)
-- ----------------------------------------------------------------------------
CREATE TABLE appointments (
    appointment_id     INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(20) NOT NULL UNIQUE,
    patient_id         INT NOT NULL,
    dentist_id         INT NOT NULL,
    treatment_id       INT NOT NULL,
    appointment_date   DATE NOT NULL,
    appointment_time   TIME NOT NULL,
    status             ENUM('SCHEDULED','COMPLETED','CANCELLED') DEFAULT 'SCHEDULED',
    created_by         INT,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appt_patient   FOREIGN KEY (patient_id)   REFERENCES patients(patient_id),
    CONSTRAINT fk_appt_dentist   FOREIGN KEY (dentist_id)   REFERENCES dentists(dentist_id),
    CONSTRAINT fk_appt_treatment FOREIGN KEY (treatment_id) REFERENCES treatments(treatment_id),
    CONSTRAINT fk_appt_user      FOREIGN KEY (created_by)   REFERENCES users(user_id)
);
CREATE INDEX idx_appt_date ON appointments(appointment_date);
CREATE INDEX idx_appt_dentist_date ON appointments(dentist_id, appointment_date);

-- ----------------------------------------------------------------------------
-- BILLS  (one bill per appointment - enforced by a UNIQUE constraint so a
-- second "Generate Bill" click can never create a duplicate row)
-- ----------------------------------------------------------------------------
CREATE TABLE bills (
    bill_id        INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL UNIQUE,
    base_fee       DECIMAL(10,2) NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    total_amount   DECIMAL(10,2) NOT NULL,
    payment_status ENUM('UNPAID','PAID') DEFAULT 'UNPAID',
    generated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

-- ----------------------------------------------------------------------------
-- AUDIT LOG  (populated by trigger - demonstrates traceability for testing)
-- ----------------------------------------------------------------------------
CREATE TABLE audit_log (
    log_id      INT AUTO_INCREMENT PRIMARY KEY,
    table_name  VARCHAR(50),
    action      VARCHAR(20),
    reference_id INT,
    log_message TEXT,
    logged_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- FUNCTION: fn_calculate_total
-- ============================================================================
DELIMITER $$
CREATE FUNCTION fn_calculate_total(
    p_base_fee DECIMAL(10,2),
    p_consultation_fee DECIMAL(10,2)
) RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    RETURN (p_base_fee + p_consultation_fee);
END $$
DELIMITER ;

-- ============================================================================
-- TRIGGER: trg_prevent_double_booking
-- Overlap-aware: each treatment has its own duration_minutes, so this checks
-- whether the new appointment's [start, start+duration) window overlaps ANY
-- existing SCHEDULED appointment's window for the same dentist on the same
-- date, rather than only rejecting an exact time match. The application
-- layer also checks slot availability before this ever fires (see
-- AvailableSlotsHandler) - this trigger is the DB-level safety net.
-- ============================================================================
DELIMITER $$
CREATE TRIGGER trg_prevent_double_booking
BEFORE INSERT ON appointments
FOR EACH ROW
BEGIN
    DECLARE new_duration INT;
    DECLARE new_end TIME;
    DECLARE overlap_count INT;

    SELECT duration_minutes INTO new_duration FROM treatments WHERE treatment_id = NEW.treatment_id;
    SET new_end = ADDTIME(NEW.appointment_time, SEC_TO_TIME(new_duration * 60));

    SELECT COUNT(*) INTO overlap_count
    FROM appointments a
    JOIN treatments t ON a.treatment_id = t.treatment_id
    WHERE a.dentist_id = NEW.dentist_id
      AND a.appointment_date = NEW.appointment_date
      AND a.status = 'SCHEDULED'
      AND a.appointment_time < new_end
      AND ADDTIME(a.appointment_time, SEC_TO_TIME(t.duration_minutes * 60)) > NEW.appointment_time;

    IF overlap_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Double booking: this dentist already has an overlapping appointment at that date/time.';
    END IF;
END $$
DELIMITER ;

-- ============================================================================
-- TRIGGER: trg_audit_bill_insert
-- ============================================================================
DELIMITER $$
CREATE TRIGGER trg_audit_bill_insert
AFTER INSERT ON bills
FOR EACH ROW
BEGIN
    INSERT INTO audit_log(table_name, action, reference_id, log_message)
    VALUES ('bills', 'INSERT', NEW.bill_id,
            CONCAT('Bill #', NEW.bill_id, ' generated for appointment #', NEW.appointment_id,
                   ' total=', NEW.total_amount));
END $$
DELIMITER ;

-- ============================================================================
-- STORED PROCEDURE: sp_register_appointment  (NEW patient + appointment)
-- ============================================================================
DELIMITER $$
CREATE PROCEDURE sp_register_appointment(
    IN  p_patient_name   VARCHAR(100),
    IN  p_address        VARCHAR(255),
    IN  p_contact_number VARCHAR(20),
    IN  p_birth_year     INT,
    IN  p_gender         VARCHAR(10),
    IN  p_dentist_id     INT,
    IN  p_treatment_id   INT,
    IN  p_appt_date      DATE,
    IN  p_appt_time      TIME,
    IN  p_created_by     INT,
    OUT p_appointment_number VARCHAR(20)
)
BEGIN
    DECLARE v_patient_id INT;
    DECLARE v_next_seq INT;

    START TRANSACTION;

    INSERT INTO patients(patient_name, address, contact_number, birth_year, gender)
    VALUES (p_patient_name, p_address, p_contact_number, p_birth_year, p_gender);
    SET v_patient_id = LAST_INSERT_ID();

    SELECT IFNULL(MAX(appointment_id), 0) + 1 INTO v_next_seq FROM appointments;
    SET p_appointment_number = CONCAT('APT', LPAD(v_next_seq, 5, '0'));

    INSERT INTO appointments(appointment_number, patient_id, dentist_id, treatment_id,
                              appointment_date, appointment_time, created_by)
    VALUES (p_appointment_number, v_patient_id, p_dentist_id, p_treatment_id,
            p_appt_date, p_appt_time, p_created_by);

    COMMIT;
END $$
DELIMITER ;

-- ============================================================================
-- STORED PROCEDURE: sp_register_appointment_existing (EXISTING patient)
-- ============================================================================
DELIMITER $$
CREATE PROCEDURE sp_register_appointment_existing(
    IN  p_patient_id     INT,
    IN  p_dentist_id     INT,
    IN  p_treatment_id   INT,
    IN  p_appt_date      DATE,
    IN  p_appt_time      TIME,
    IN  p_created_by     INT,
    OUT p_appointment_number VARCHAR(20)
)
BEGIN
    DECLARE v_next_seq INT;

    START TRANSACTION;

    SELECT IFNULL(MAX(appointment_id), 0) + 1 INTO v_next_seq FROM appointments;
    SET p_appointment_number = CONCAT('APT', LPAD(v_next_seq, 5, '0'));

    INSERT INTO appointments(appointment_number, patient_id, dentist_id, treatment_id,
                              appointment_date, appointment_time, created_by)
    VALUES (p_appointment_number, p_patient_id, p_dentist_id, p_treatment_id,
            p_appt_date, p_appt_time, p_created_by);

    COMMIT;
END $$
DELIMITER ;

-- ============================================================================
-- STORED PROCEDURE: sp_generate_bill
-- One bill per appointment: if a bill already exists for this appointment,
-- returns its existing id/total instead of inserting a duplicate row (the
-- UNIQUE constraint on bills.appointment_id is the hard guarantee; this
-- check just avoids throwing on a routine repeat click).
-- Consultation fee now comes from the DENTIST, not the treatment.
-- ============================================================================
DELIMITER $$
CREATE PROCEDURE sp_generate_bill(
    IN p_appointment_id INT,
    OUT p_bill_id INT,
    OUT p_total DECIMAL(10,2)
)
BEGIN
    DECLARE v_existing_bill_id INT DEFAULT NULL;
    DECLARE v_base_fee DECIMAL(10,2);
    DECLARE v_consultation_fee DECIMAL(10,2);

    SELECT bill_id, total_amount INTO v_existing_bill_id, p_total
      FROM bills WHERE appointment_id = p_appointment_id LIMIT 1;

    IF v_existing_bill_id IS NOT NULL THEN
        SET p_bill_id = v_existing_bill_id;
    ELSE
        SELECT t.base_fee, d.consultation_fee
          INTO v_base_fee, v_consultation_fee
          FROM appointments a
          JOIN treatments t ON a.treatment_id = t.treatment_id
          JOIN dentists d ON a.dentist_id = d.dentist_id
         WHERE a.appointment_id = p_appointment_id;

        SET p_total = fn_calculate_total(v_base_fee, v_consultation_fee);

        INSERT INTO bills(appointment_id, base_fee, consultation_fee, total_amount)
        VALUES (p_appointment_id, v_base_fee, v_consultation_fee, p_total);

        SET p_bill_id = LAST_INSERT_ID();
    END IF;
END $$
DELIMITER ;

-- ============================================================================
-- STORED PROCEDURE: sp_update_appointment_status
-- ============================================================================
DELIMITER $$
CREATE PROCEDURE sp_update_appointment_status(
    IN p_appointment_id INT,
    IN p_new_status VARCHAR(20)
)
BEGIN
    UPDATE appointments SET status = p_new_status WHERE appointment_id = p_appointment_id;
END $$
DELIMITER ;

-- ============================================================================
-- REPORT VIEWS (unfiltered - kept for quick ad-hoc inspection in Workbench;
-- the Reports screen itself uses date-ranged parameterised queries instead,
-- see ReportDAOImpl.java)
-- ============================================================================
CREATE VIEW vw_daily_schedule AS
SELECT a.appointment_number, a.appointment_date, a.appointment_time,
       p.patient_name, d.dentist_name, t.treatment_name, a.status
FROM appointments a
JOIN patients p ON a.patient_id = p.patient_id
JOIN dentists d ON a.dentist_id = d.dentist_id
JOIN treatments t ON a.treatment_id = t.treatment_id;

CREATE VIEW vw_revenue_by_treatment AS
SELECT t.treatment_name, COUNT(b.bill_id) AS bills_issued, SUM(b.total_amount) AS total_revenue
FROM bills b
JOIN appointments a ON b.appointment_id = a.appointment_id
JOIN treatments t ON a.treatment_id = t.treatment_id
GROUP BY t.treatment_name;

CREATE VIEW vw_dentist_workload AS
SELECT d.dentist_name, COUNT(a.appointment_id) AS total_appointments,
       SUM(CASE WHEN a.status='COMPLETED' THEN 1 ELSE 0 END) AS completed
FROM dentists d
LEFT JOIN appointments a ON d.dentist_id = a.dentist_id
GROUP BY d.dentist_name;

CREATE VIEW vw_outstanding_bills AS
SELECT b.bill_id, a.appointment_number, p.patient_name, b.total_amount, b.generated_at
FROM bills b
JOIN appointments a ON b.appointment_id = a.appointment_id
JOIN patients p ON a.patient_id = p.patient_id
WHERE b.payment_status = 'UNPAID';

-- ============================================================================
-- SEED DATA
-- ============================================================================
-- Default login -> username: admin  / password: Admin@123
-- Generate the real hash with PasswordUtil (see README.md) and replace this
-- placeholder before logging in.
INSERT INTO users(username, password_hash, full_name, birth_year, gender, role) VALUES
('admin', 'REPLACE_WITH_PASSWORDUTIL_OUTPUT', 'Clinic Admin', 1990, 'OTHER', 'ADMIN');

INSERT INTO dentists(dentist_name, specialization, consultation_fee) VALUES
('Dr. Nadeesha Perera', 'General Dentistry', 1000.00),
('Dr. Kasun Fernando', 'Orthodontics', 1500.00),
('Dr. Ishara Silva', 'Oral Surgery', 2000.00);

INSERT INTO treatments(treatment_name, base_fee, duration_minutes) VALUES
('Routine Check-up', 1500.00, 30),
('Scaling & Polishing', 3500.00, 45),
('Tooth Filling', 5000.00, 45),
('Root Canal Treatment', 15000.00, 90),
('Tooth Extraction', 4000.00, 45),
('Braces Consultation', 2500.00, 30);

   CREATE USER 'dental_app'@'localhost' IDENTIFIED BY 'DentalApp#2026';
   GRANT ALL PRIVILEGES ON sunrise_dental.* TO 'dental_app'@'localhost';
   FLUSH PRIVILEGES;
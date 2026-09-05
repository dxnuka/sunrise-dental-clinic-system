-- ============================================================================
-- Migration v3: run this if you already have a v1 or v2 sunrise_dental
-- database and want to keep your data (if upgrading straight from v1, run
-- migration_v2.sql FIRST, then this one). Covers:
--   - per-dentist consultation fee (replacing the flat per-treatment fee)
--   - patient birth year / gender
--   - one bill per appointment (dedupes any existing duplicate bill rows)
--   - removes the loyalty-discount column/logic entirely
--   - appointment status update stored procedure
-- ============================================================================
USE sunrise_dental;

-- ---- dentists: add per-dentist consultation fee ----
ALTER TABLE dentists
    ADD COLUMN consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 1000.00 AFTER specialization;
-- Customize these to taste:
UPDATE dentists SET consultation_fee = 1000.00 WHERE dentist_name = 'Dr. Nadeesha Perera';
UPDATE dentists SET consultation_fee = 1500.00 WHERE dentist_name = 'Dr. Kasun Fernando';
UPDATE dentists SET consultation_fee = 2000.00 WHERE dentist_name = 'Dr. Ishara Silva';

-- ---- treatments: consultation_fee moves to dentists, drop it here ----
ALTER TABLE treatments DROP COLUMN consultation_fee;

-- ---- patients: add demographic fields ----
ALTER TABLE patients
    ADD COLUMN birth_year INT NULL AFTER contact_number,
    ADD COLUMN gender ENUM('MALE','FEMALE','OTHER') NULL AFTER birth_year;

-- ---- bills: dedupe any existing duplicate rows (keep the earliest bill per
-- appointment), drop the unused discount column, then enforce one-bill-per-
-- appointment going forward ----
DELETE b1 FROM bills b1
INNER JOIN bills b2
  ON b1.appointment_id = b2.appointment_id
 AND b1.bill_id > b2.bill_id;

ALTER TABLE bills DROP COLUMN discount;
ALTER TABLE bills ADD CONSTRAINT uq_bill_appointment UNIQUE (appointment_id);

-- ---- fn_calculate_total: drop the 3-arg (with discount) version, recreate as 2-arg ----
DROP FUNCTION IF EXISTS fn_calculate_total;
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

-- ---- sp_register_appointment: add birth_year/gender params ----
DROP PROCEDURE IF EXISTS sp_register_appointment;
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

-- ---- sp_generate_bill: dentist-based consultation fee + one-bill-per-appointment ----
DROP PROCEDURE IF EXISTS sp_generate_bill;
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

-- ---- new: appointment status update ----
DROP PROCEDURE IF EXISTS sp_update_appointment_status;
DELIMITER $$
CREATE PROCEDURE sp_update_appointment_status(
    IN p_appointment_id INT,
    IN p_new_status VARCHAR(20)
)
BEGIN
    UPDATE appointments SET status = p_new_status WHERE appointment_id = p_appointment_id;
END $$
DELIMITER ;

-- Verify:
-- DESCRIBE dentists; DESCRIBE patients; DESCRIBE bills;
-- SELECT appointment_id, COUNT(*) FROM bills GROUP BY appointment_id HAVING COUNT(*) > 1; -- should return 0 rows

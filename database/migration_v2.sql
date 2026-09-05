-- ============================================================================
-- Migration v2: run this INSTEAD of schema.sql if you already have a working
-- sunrise_dental database and don't want to lose your data / re-hash your
-- admin password. Safe to run once against an existing v1 database.
-- ============================================================================
USE sunrise_dental;

-- ---- users: add profile fields ----
ALTER TABLE users
    ADD COLUMN birth_year INT NULL AFTER full_name,
    ADD COLUMN gender ENUM('MALE','FEMALE','OTHER') NULL AFTER birth_year;

-- ---- treatments: add duration, used for overlap-aware scheduling ----
ALTER TABLE treatments
    ADD COLUMN duration_minutes INT NOT NULL DEFAULT 30 AFTER consultation_fee;

UPDATE treatments SET duration_minutes = 30 WHERE treatment_name = 'Routine Check-up';
UPDATE treatments SET duration_minutes = 45 WHERE treatment_name = 'Scaling & Polishing';
UPDATE treatments SET duration_minutes = 45 WHERE treatment_name = 'Tooth Filling';
UPDATE treatments SET duration_minutes = 90 WHERE treatment_name = 'Root Canal Treatment';
UPDATE treatments SET duration_minutes = 45 WHERE treatment_name = 'Tooth Extraction';
UPDATE treatments SET duration_minutes = 30 WHERE treatment_name = 'Braces Consultation';

-- ---- helpful indexes for search/filter/pagination ----
CREATE INDEX idx_patients_name ON patients(patient_name);
CREATE INDEX idx_patients_contact ON patients(contact_number);
CREATE INDEX idx_appt_date ON appointments(appointment_date);
CREATE INDEX idx_appt_dentist_date ON appointments(dentist_id, appointment_date);

-- ---- replace the exact-time-match trigger with an overlap-aware one ----
DROP TRIGGER IF EXISTS trg_prevent_double_booking;

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

-- ---- new stored procedure for booking an EXISTING patient (no new patient row) ----
DROP PROCEDURE IF EXISTS sp_register_appointment_existing;

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

-- Verify:
-- SELECT * FROM treatments;
-- DESCRIBE users;

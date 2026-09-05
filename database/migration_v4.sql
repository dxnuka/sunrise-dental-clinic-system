-- ============================================================================
-- Migration v4: run this if you already have a v1/v2/v3 sunrise_dental
-- database (run v2 -> v3 -> v4 in order if starting from v1). Covers:
--   - lets an admin delete a receptionist account without breaking that
--     receptionist's historical appointments (created_by is set to NULL
--     instead of the delete being blocked by the foreign key)
-- No new columns are needed for the admin/receptionist role split itself -
-- the users.role ENUM('ADMIN','RECEPTIONIST') already supports it.
-- ============================================================================
USE sunrise_dental;

ALTER TABLE appointments DROP FOREIGN KEY fk_appt_user;
ALTER TABLE appointments
    ADD CONSTRAINT fk_appt_user FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL;

-- Verify: SHOW CREATE TABLE appointments; -- fk_appt_user should show ON DELETE SET NULL

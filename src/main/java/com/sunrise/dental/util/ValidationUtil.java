package com.sunrise.dental.util;

import com.sunrise.dental.exception.ValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;

/**
 * Centralised input validation used by every form-handling class in the
 * application ("restrict invalid entries to the system"). Each method
 * throws a ValidationException with a clear, field-specific message rather
 * than letting bad data reach the service/DAO layers.
 */
public class ValidationUtil {

    // Letters (incl. accented), spaces and hyphens only - rejects digits,
    // apostrophes, and other special characters in name fields.
    private static final String NAME_PATTERN = "^[\\p{L} \\-]{2,100}$";
    // Exactly 10 digits, nothing else (no spaces, dashes, letters, symbols).
    private static final String PHONE_PATTERN = "^[0-9]{10}$";
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_.]{4,30}$";

    public static void requireNonBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
    }

    /** Names: letters, spaces and hyphens only, 2-100 chars - no digits or symbols. */
    public static void requireValidName(String value, String fieldName) throws ValidationException {
        requireNonBlank(value, fieldName);
        if (!value.trim().matches(NAME_PATTERN)) {
            throw new ValidationException(fieldName + " can only contain letters, spaces and hyphens (no numbers or symbols).");
        }
    }

    /** Contact numbers: exactly 10 digits, no spaces/dashes/letters. */
    public static void requireValidPhone(String phone) throws ValidationException {
        requireNonBlank(phone, "Contact number");
        if (!phone.trim().matches(PHONE_PATTERN)) {
            throw new ValidationException("Contact number must be exactly 10 digits (numbers only).");
        }
    }

    public static void requireValidAddress(String address) throws ValidationException {
        requireNonBlank(address, "Address");
        if (address.trim().length() < 5 || address.trim().length() > 255) {
            throw new ValidationException("Address must be between 5 and 255 characters.");
        }
    }

    public static void requireValidUsername(String username) throws ValidationException {
        requireNonBlank(username, "Username");
        if (!username.trim().matches(USERNAME_PATTERN)) {
            throw new ValidationException("Username must be 4-30 characters: letters, numbers, dots or underscores only.");
        }
    }

    public static void requireValidPassword(String password) throws ValidationException {
        requireNonBlank(password, "Password");
        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
    }

    /** Birth year: a plausible 4-digit year, not in the future, and not implausibly old. */
    public static void requireValidBirthYear(Integer birthYear) throws ValidationException {
        if (birthYear == null) throw new ValidationException("Birth year is required.");
        int currentYear = Year.now().getValue();
        if (birthYear < 1900 || birthYear > currentYear) {
            throw new ValidationException("Birth year must be between 1900 and " + currentYear + ".");
        }
    }

    public static void requireValidGender(String gender) throws ValidationException {
        requireNonBlank(gender, "Gender");
        if (!gender.equals("MALE") && !gender.equals("FEMALE") && !gender.equals("OTHER")) {
            throw new ValidationException("Please select a valid gender option.");
        }
    }

    /** Date must be today or in the future (never a past calendar date). */
    public static void requireFutureOrTodayDate(LocalDate date) throws ValidationException {
        if (date == null) throw new ValidationException("Appointment date is required.");
        if (date.isBefore(LocalDate.now())) {
            throw new ValidationException("Appointment date cannot be in the past.");
        }
    }

    /** Selectable times are fixed 15-minute blocks between 08:00 and 17:00 inclusive. */
    public static void requireValidTimeSlot(LocalTime time) throws ValidationException {
        if (time == null) throw new ValidationException("Appointment time is required.");
        if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(17, 0))) {
            throw new ValidationException("Appointments must be scheduled between 08:00 and 17:00.");
        }
        if (time.getMinute() % 15 != 0) {
            throw new ValidationException("Appointment times must fall on a 15-minute block.");
        }
    }

    /** Rejects a date+time combination that has already passed, even if the date alone is today. */
    public static void requireNotPastDateTime(LocalDate date, LocalTime time) throws ValidationException {
        if (date == null || time == null) return; // individual field checks already cover nulls
        if (LocalDateTime.of(date, time).isBefore(LocalDateTime.now())) {
            throw new ValidationException("That date and time has already passed. Please choose a future slot.");
        }
    }

    public static void requirePositiveId(Integer id, String fieldName) throws ValidationException {
        if (id == null || id <= 0) throw new ValidationException("Please select a valid " + fieldName + ".");
    }
}

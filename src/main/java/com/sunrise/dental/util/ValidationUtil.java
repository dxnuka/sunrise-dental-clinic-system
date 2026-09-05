package com.sunrise.dental.util;

import com.sunrise.dental.exception.ValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;

public class ValidationUtil {

    private static final String NAME_PATTERN = "^[\\p{L} \\-]{2,100}$";
    private static final String DENTIST_NAME_PATTERN = "^[\\p{L} .\\-]{2,100}$";
    private static final String PHONE_PATTERN = "^[0-9]{10}$";
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_.]{4,30}$";

    public static void requireNonBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
    }

    public static void requireValidName(String value, String fieldName) throws ValidationException {
        requireNonBlank(value, fieldName);
        if (!value.trim().matches(NAME_PATTERN)) {
            throw new ValidationException(fieldName + " can only contain letters, spaces and hyphens (no numbers or symbols).");
        }
    }

    public static void requireValidDentistName(String value) throws ValidationException {
        requireNonBlank(value, "Dentist name");
        if (!value.trim().matches(DENTIST_NAME_PATTERN)) {
            throw new ValidationException("Dentist name can only contain letters, spaces, hyphens and periods.");
        }
    }

    public static void requireValidFee(java.math.BigDecimal value, String fieldName) throws ValidationException {
        if (value == null || value.signum() <= 0) {
            throw new ValidationException(fieldName + " must be a positive amount.");
        }
    }

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

    public static void requireFutureOrTodayDate(LocalDate date) throws ValidationException {
        if (date == null) throw new ValidationException("Appointment date is required.");
        if (date.isBefore(LocalDate.now())) {
            throw new ValidationException("Appointment date cannot be in the past.");
        }
    }

    public static void requireValidTimeSlot(LocalTime time) throws ValidationException {
        if (time == null) throw new ValidationException("Appointment time is required.");
        if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(17, 0))) {
            throw new ValidationException("Appointments must be scheduled between 08:00 and 17:00.");
        }
        if (time.getMinute() % 15 != 0) {
            throw new ValidationException("Appointment times must fall on a 15-minute block.");
        }
    }

    public static void requireNotPastDateTime(LocalDate date, LocalTime time) throws ValidationException {
        if (date == null || time == null) return;
        if (LocalDateTime.of(date, time).isBefore(LocalDateTime.now())) {
            throw new ValidationException("That date and time has already passed. Please choose a future slot.");
        }
    }

    public static void requirePositiveId(Integer id, String fieldName) throws ValidationException {
        if (id == null || id <= 0) throw new ValidationException("Please select a valid " + fieldName + ".");
    }
}

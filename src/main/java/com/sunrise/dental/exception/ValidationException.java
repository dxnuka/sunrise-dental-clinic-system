package com.sunrise.dental.exception;

/** Thrown when user-supplied input fails validation before it reaches the DB. */
public class ValidationException extends Exception {
    public ValidationException(String message) { super(message); }
}

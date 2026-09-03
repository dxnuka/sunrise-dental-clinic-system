package com.sunrise.dental.exception;

/** Thrown when a new appointment would clash with an existing one for the same
 *  dentist at the same date/time - the core business rule from the scenario. */
public class DoubleBookingException extends Exception {
    public DoubleBookingException(String message) { super(message); }
}

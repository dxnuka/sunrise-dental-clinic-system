package com.sunrise.dental.observer;

import com.sunrise.dental.model.Appointment;

/**
 * ---------------------------------------------------------------------------
 * DESIGN PATTERN: OBSERVER
 * ---------------------------------------------------------------------------
 * Any class implementing this interface can subscribe to appointment events
 * (see AppointmentEventPublisher). This is where a future SMS/email reminder
 * feature plugs in without AppointmentService needing to change - it simply
 * publishes an event and does not care who is listening. See docs/design-
 * patterns.md and the "Future Features" section of README.md.
 * ---------------------------------------------------------------------------
 */
public interface AppointmentEventListener {
    void onAppointmentRegistered(Appointment appointment);
}

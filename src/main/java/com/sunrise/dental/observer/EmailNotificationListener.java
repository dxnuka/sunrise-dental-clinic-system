package com.sunrise.dental.observer;

import com.sunrise.dental.model.Appointment;

/**
 * Example concrete Observer. Currently just logs to the console/server log;
 * a real deployment would call an email API here. Demonstrates that new
 * notification channels (SMS, push) can be added as new listener classes
 * with zero changes to AppointmentService.
 */
public class EmailNotificationListener implements AppointmentEventListener {
    @Override
    public void onAppointmentRegistered(Appointment appointment) {
        System.out.println("[EmailNotificationListener] Confirmation email queued for appointment "
                + appointment.getAppointmentNumber() + " (" + appointment.getPatient().getPatientName() + ")");
    }
}

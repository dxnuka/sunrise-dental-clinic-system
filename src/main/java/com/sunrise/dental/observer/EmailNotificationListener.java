package com.sunrise.dental.observer;

import com.sunrise.dental.model.Appointment;

public class EmailNotificationListener implements AppointmentEventListener {
    @Override
    public void onAppointmentRegistered(Appointment appointment) {
        System.out.println("[EmailNotificationListener] Confirmation email queued for appointment "
                + appointment.getAppointmentNumber() + " (" + appointment.getPatient().getPatientName() + ")");
    }
}

package com.sunrise.dental.observer;

import com.sunrise.dental.model.Appointment;
import java.util.ArrayList;
import java.util.List;

public class AppointmentEventPublisher {
    private static final List<AppointmentEventListener> LISTENERS = new ArrayList<>();

    static {
        LISTENERS.add(new EmailNotificationListener());
    }

    public static void subscribe(AppointmentEventListener listener) { LISTENERS.add(listener); }

    public static void publishAppointmentRegistered(Appointment appointment) {
        for (AppointmentEventListener l : LISTENERS) {
            try {
                l.onAppointmentRegistered(appointment);
            } catch (Exception e) {
                System.err.println("[AppointmentEventPublisher] " + l.getClass().getSimpleName()
                        + " failed to handle the appointment-registered event: " + e.getMessage());
            }
        }
    }
}
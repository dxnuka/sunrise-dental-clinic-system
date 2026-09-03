package com.sunrise.dental.observer;

import com.sunrise.dental.model.Appointment;
import java.util.ArrayList;
import java.util.List;

/** The Subject in the Observer pattern - keeps a list of listeners and notifies them all. */
public class AppointmentEventPublisher {
    private static final List<AppointmentEventListener> LISTENERS = new ArrayList<>();

    static {
        // Register default listeners here. Add SmsNotificationListener,
        // PushNotificationListener etc. later with no other code changes.
        LISTENERS.add(new EmailNotificationListener());
    }

    public static void subscribe(AppointmentEventListener listener) { LISTENERS.add(listener); }

    public static void publishAppointmentRegistered(Appointment appointment) {
        for (AppointmentEventListener l : LISTENERS) l.onAppointmentRegistered(appointment);
    }
}

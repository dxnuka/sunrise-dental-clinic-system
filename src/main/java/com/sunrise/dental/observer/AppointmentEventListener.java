package com.sunrise.dental.observer;

import com.sunrise.dental.model.Appointment;

public interface AppointmentEventListener {
    void onAppointmentRegistered(Appointment appointment);
}

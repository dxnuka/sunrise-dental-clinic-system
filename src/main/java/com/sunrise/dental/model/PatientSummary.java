package com.sunrise.dental.model;

public class PatientSummary {
    private Patient patient;
    private int totalAppointments;
    private Appointment lastAppointment;   // most recent past/completed appointment, or null
    private Appointment nextAppointment;   // nearest upcoming SCHEDULED appointment, or null

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public int getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(int totalAppointments) { this.totalAppointments = totalAppointments; }
    public Appointment getLastAppointment() { return lastAppointment; }
    public void setLastAppointment(Appointment lastAppointment) { this.lastAppointment = lastAppointment; }
    public Appointment getNextAppointment() { return nextAppointment; }
    public void setNextAppointment(Appointment nextAppointment) { this.nextAppointment = nextAppointment; }
}

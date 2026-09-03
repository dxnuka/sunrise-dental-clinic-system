package com.sunrise.dental.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Aggregates a patient + dentist + treatment + schedule slot.
 * This is the central entity of the system (matches the "unique appointment
 * number per patient visit" requirement in the assessment brief).
 */
public class Appointment {
    private int appointmentId;
    private String appointmentNumber;
    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

    public Appointment() {}

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public String getAppointmentNumber() { return appointmentNumber; }
    public void setAppointmentNumber(String appointmentNumber) { this.appointmentNumber = appointmentNumber; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Dentist getDentist() { return dentist; }
    public void setDentist(Dentist dentist) { this.dentist = dentist; }
    public Treatment getTreatment() { return treatment; }
    public void setTreatment(Treatment treatment) { this.treatment = treatment; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

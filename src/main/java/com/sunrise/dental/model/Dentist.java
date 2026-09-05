package com.sunrise.dental.model;

import java.math.BigDecimal;

public class Dentist {
    private int dentistId;
    private String dentistName;
    private String specialization;
    private BigDecimal consultationFee;
    private boolean active = true;

    public Dentist() {}

    public Dentist(int dentistId, String dentistName, String specialization, BigDecimal consultationFee) {
        this.dentistId = dentistId;
        this.dentistName = dentistName;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }
    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() { return dentistName; }
}

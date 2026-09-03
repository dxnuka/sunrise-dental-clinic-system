package com.sunrise.dental.model;

import java.math.BigDecimal;

public class Treatment {
    private int treatmentId;
    private String treatmentName;
    private BigDecimal baseFee;
    private int durationMinutes;

    public Treatment() {}

    public Treatment(int treatmentId, String treatmentName, BigDecimal baseFee, int durationMinutes) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.baseFee = baseFee;
        this.durationMinutes = durationMinutes;
    }

    public int getTreatmentId() { return treatmentId; }
    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }
    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
    public BigDecimal getBaseFee() { return baseFee; }
    public void setBaseFee(BigDecimal baseFee) { this.baseFee = baseFee; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    @Override
    public String toString() { return treatmentName; }
}

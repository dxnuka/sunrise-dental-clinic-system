package com.sunrise.dental.service;

import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.model.Treatment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TEST PLAN - BillingService / Strategy pattern.
 * previewTotal() has no DB dependency at all, so these are pure, extremely
 * fast unit tests validating the fee-calculation rule: total = treatment
 * base fee + the TREATING DENTIST's own consultation fee (consultation fees
 * differ doctor to doctor, mirroring the sp_generate_bill stored procedure).
 */
class BillingServiceTest {

    private final BillingService billingService = new BillingService(null); // no DAO needed for previewTotal()

    private Treatment sampleTreatment() {
        return new Treatment(1, "Root Canal Treatment", new BigDecimal("15000.00"), 90);
    }

    private Dentist dentistWithFee(String fee) {
        return new Dentist(1, "Dr. Ishara Silva", "Oral Surgery", new BigDecimal(fee));
    }

    @Test
    @DisplayName("Total is the treatment's base fee plus the dentist's consultation fee")
    void previewTotal_addsBaseAndConsultationFee() {
        BigDecimal total = billingService.previewTotal(sampleTreatment(), dentistWithFee("1000.00"));
        assertEquals(new BigDecimal("16000.00"), total);
    }

    @Test
    @DisplayName("A different dentist's higher consultation fee changes the total for the same treatment")
    void previewTotal_variesByDentistConsultationFee() {
        BigDecimal total = billingService.previewTotal(sampleTreatment(), dentistWithFee("2000.00"));
        assertEquals(new BigDecimal("17000.00"), total);
    }

    @Test
    @DisplayName("A lower consultation fee likewise lowers the total")
    void previewTotal_lowerConsultationFee() {
        BigDecimal total = billingService.previewTotal(sampleTreatment(), dentistWithFee("500.00"));
        assertEquals(new BigDecimal("15500.00"), total);
    }
}

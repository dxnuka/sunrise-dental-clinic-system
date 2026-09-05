package com.sunrise.dental.billing;

import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.model.Treatment;
import java.math.BigDecimal;

public class StandardBillingStrategy implements BillingStrategy {
    @Override
    public BigDecimal calculateTotal(Treatment treatment, Dentist dentist) {
        return treatment.getBaseFee().add(dentist.getConsultationFee());
    }
}

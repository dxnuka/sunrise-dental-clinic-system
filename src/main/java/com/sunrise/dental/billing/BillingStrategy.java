package com.sunrise.dental.billing;

import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.model.Treatment;
import java.math.BigDecimal;

public interface BillingStrategy {
    BigDecimal calculateTotal(Treatment treatment, Dentist dentist);
}

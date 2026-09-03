package com.sunrise.dental.billing;

import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.model.Treatment;
import java.math.BigDecimal;

/**
 * ---------------------------------------------------------------------------
 * DESIGN PATTERN: STRATEGY
 * ---------------------------------------------------------------------------
 * Defines a family of interchangeable fee-calculation algorithms. The actual
 * calculation lives in the database (fn_calculate_total / sp_generate_bill)
 * for data integrity, but the SAME rule is mirrored here in Java so the
 * business logic tier can preview a bill before it is committed, and so the
 * rule is unit-testable in isolation (see BillingServiceTest). Only one
 * concrete strategy exists today (StandardBillingStrategy), but the
 * interface is kept as the extension point for a future differential rule
 * (e.g. an insurance- or senior-citizen-specific strategy) without touching
 * BillingService's callers.
 * ---------------------------------------------------------------------------
 */
public interface BillingStrategy {
    BigDecimal calculateTotal(Treatment treatment, Dentist dentist);
}

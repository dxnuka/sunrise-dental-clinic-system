package com.sunrise.dental.service;

import com.sunrise.dental.billing.BillingStrategy;
import com.sunrise.dental.billing.StandardBillingStrategy;
import com.sunrise.dental.dao.BillDAO;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.factory.DAOFactory;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.model.Treatment;

import java.math.BigDecimal;

/** Business Logic Tier for billing. Delegates the authoritative calculation +
 *  persistence to the database via BillDAO (sp_generate_bill), which also
 *  guarantees exactly one bill per appointment - repeat clicks return the
 *  same bill instead of creating duplicates. */
public class BillingService {

    private final BillDAO billDAO;
    private final BillingStrategy billingStrategy = new StandardBillingStrategy();

    public BillingService() { this(DAOFactory.getBillDAO()); }
    public BillingService(BillDAO billDAO) { this.billDAO = billDAO; }

    /** Pure calculation, no DB write - used to preview a total, and unit tested directly. */
    public BigDecimal previewTotal(Treatment treatment, Dentist dentist) {
        return billingStrategy.calculateTotal(treatment, dentist);
    }

    /** Generates (or, if one already exists for this appointment, simply returns) the bill. */
    public Bill generateBill(Appointment appointment) throws ValidationException {
        if (appointment == null) throw new ValidationException("Appointment not found - cannot generate a bill.");
        Bill bill = billDAO.generateBill(appointment.getAppointmentId());
        bill.setAppointmentNumber(appointment.getAppointmentNumber());
        return bill;
    }
}

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

public class BillingService {

    private final BillDAO billDAO;
    private final BillingStrategy billingStrategy = new StandardBillingStrategy();

    public BillingService() { this(DAOFactory.getBillDAO()); }
    public BillingService(BillDAO billDAO) { this.billDAO = billDAO; }

    public BigDecimal previewTotal(Treatment treatment, Dentist dentist) {
        return billingStrategy.calculateTotal(treatment, dentist);
    }

    public Bill generateBill(Appointment appointment) throws ValidationException {
        if (appointment == null) throw new ValidationException("Appointment not found - cannot generate a bill.");
        Bill bill = billDAO.generateBill(appointment.getAppointmentId());
        bill.setAppointmentNumber(appointment.getAppointmentNumber());
        return bill;
    }
}

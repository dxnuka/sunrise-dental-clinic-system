package com.sunrise.dental.dao;

import com.sunrise.dental.model.Bill;
import java.util.List;

public interface BillDAO {
    /** Calls sp_generate_bill, which itself fires trg_audit_bill_insert. */
    Bill generateBill(int appointmentId);
    Bill findByAppointmentNumber(String appointmentNumber);
    List<Bill> findOutstanding();
}

package com.sunrise.dental.dao;

import com.sunrise.dental.model.Bill;
import java.util.List;

public interface BillDAO {
    Bill generateBill(int appointmentId);
    Bill findByAppointmentNumber(String appointmentNumber);
    List<Bill> findOutstanding();
}

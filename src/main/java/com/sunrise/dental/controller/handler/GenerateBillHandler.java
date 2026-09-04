package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.service.BillingService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class GenerateBillHandler implements RequestHandler {
    private final AppointmentService appointmentService = new AppointmentService();
    private final BillingService billingService = new BillingService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String number = request.getParameter("appointmentNumber");
        try {
            Appointment appointment = appointmentService.find(number);
            if (appointment == null) {
                MessageUtil.setWarning(session, "No appointment found with number \"" + number + "\".");
                return "bill.jsp";
            }
            Bill bill = billingService.generateBill(appointment);
            request.setAttribute("appointment", appointment);
            request.setAttribute("bill", bill);
            MessageUtil.setSuccess(session, "Bill generated and printed for " + number + ".");
        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
        } catch (Exception e) {
            MessageUtil.setError(session, "Could not generate the bill: " + e.getMessage());
        }
        return "bill.jsp";
    }
}

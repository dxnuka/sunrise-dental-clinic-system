package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Lets staff move an appointment from SCHEDULED to COMPLETED or CANCELLED
 *  from the appointment detail view. */
public class UpdateAppointmentStatusHandler implements RequestHandler {
    private final AppointmentService appointmentService = new AppointmentService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String appointmentNumber = request.getParameter("appointmentNumber");
        String newStatus = request.getParameter("newStatus");

        try {
            appointmentService.updateStatus(appointmentNumber, newStatus);
            MessageUtil.setSuccess(session, "Appointment " + appointmentNumber + " marked as " + newStatus + ".");
        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
        } catch (Exception e) {
            MessageUtil.setError(session, "Unexpected error: could not update the appointment status.");
        }

        try {
            request.setAttribute("appointment", appointmentService.find(appointmentNumber));
        } catch (ValidationException ignored) { /* handled by the message already set above */ }
        return "view-appointment.jsp";
    }
}

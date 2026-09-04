package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ViewAppointmentHandler implements RequestHandler {
    private final AppointmentService appointmentService = new AppointmentService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String number = request.getParameter("appointmentNumber");
        try {
            Appointment appointment = appointmentService.find(number);
            if (appointment == null) {
                MessageUtil.setWarning(session, "No appointment found with number \"" + number + "\".");
            } else {
                request.setAttribute("appointment", appointment);
            }
        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
        }
        return "view-appointment.jsp";
    }
}

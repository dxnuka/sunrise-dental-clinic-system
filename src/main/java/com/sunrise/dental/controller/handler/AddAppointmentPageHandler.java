package com.sunrise.dental.controller.handler;

import com.sunrise.dental.factory.DAOFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class AddAppointmentPageHandler implements RequestHandler {
    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("dentists", DAOFactory.getDentistDAO().findAllActive());
        request.setAttribute("treatments", DAOFactory.getTreatmentDAO().findAll());
        return "add-appointment.jsp";
    }
}

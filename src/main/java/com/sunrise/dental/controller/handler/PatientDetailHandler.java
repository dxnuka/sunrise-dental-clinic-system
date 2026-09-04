package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.service.PatientService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PatientDetailHandler implements RequestHandler {
    private final PatientService patientService = new PatientService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        try {
            int patientId = Integer.parseInt(request.getParameter("patientId"));
            request.setAttribute("summary", patientService.getSummary(patientId));
        } catch (ValidationException ve) {
            MessageUtil.setWarning(request.getSession(), ve.getMessage());
        } catch (NumberFormatException nfe) {
            MessageUtil.setWarning(request.getSession(), "Invalid patient reference.");
        }
        return "patient-detail.jsp";
    }
}

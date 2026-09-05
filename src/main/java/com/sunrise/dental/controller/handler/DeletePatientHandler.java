package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.service.PatientService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class DeletePatientHandler implements RequestHandler {
    private final PatientService patientService = new PatientService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        try {
            int patientId = Integer.parseInt(request.getParameter("patientId"));
            patientService.deletePatient(patientId);
            MessageUtil.setSuccess(session, "Patient record deleted.");
        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
        } catch (NumberFormatException nfe) {
            MessageUtil.setWarning(session, "Invalid patient reference.");
        } catch (Exception e) {
            MessageUtil.setError(session, "Unexpected error: could not delete the patient.");
        }
        response.sendRedirect(request.getContextPath() + "/control?action=patients");
        return null;
    }
}

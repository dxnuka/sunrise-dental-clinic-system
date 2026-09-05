package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.service.DentistService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class DeactivateDentistHandler implements RequestHandler {
    private final DentistService dentistService = new DentistService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        try {
            int dentistId = Integer.parseInt(request.getParameter("dentistId"));
            dentistService.deactivateDentist(dentistId);
            MessageUtil.setSuccess(session, "Dentist removed. Their past appointments and bills are unaffected.");
        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
        } catch (NumberFormatException nfe) {
            MessageUtil.setWarning(session, "Invalid dentist reference.");
        } catch (Exception e) {
            MessageUtil.setError(session, "Unexpected error: could not remove the dentist.");
        }
        response.sendRedirect(request.getContextPath() + "/control?action=dentistsPage");
        return null;
    }
}

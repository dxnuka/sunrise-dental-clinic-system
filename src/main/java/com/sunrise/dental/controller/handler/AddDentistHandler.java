package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.service.DentistService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;

public class AddDentistHandler implements RequestHandler {
    private final DentistService dentistService = new DentistService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        try {
            String name = request.getParameter("dentistName");
            String specialization = request.getParameter("specialization");
            BigDecimal fee = parseFee(request.getParameter("consultationFee"));

            dentistService.addDentist(name, specialization, fee);
            MessageUtil.setSuccess(session, "Dentist \"" + name + "\" added successfully.");
        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
        } catch (Exception e) {
            MessageUtil.setError(session, "Unexpected error: could not add the dentist.");
        }
        request.setAttribute("dentists", dentistService.findAll());
        return "dentists.jsp";
    }

    private BigDecimal parseFee(String s) {
        try { return (s == null || s.trim().isEmpty()) ? null : new BigDecimal(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}

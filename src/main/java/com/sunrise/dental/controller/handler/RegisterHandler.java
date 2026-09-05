package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.service.AuthService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


public class RegisterHandler implements RequestHandler {
    private final AuthService authService = new AuthService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String gender = request.getParameter("gender");
        Integer birthYear = parseIntOrNull(request.getParameter("birthYear"));

        try {
            if (password != null && !password.equals(confirmPassword)) {
                throw new ValidationException("Password and confirmation do not match.");
            }
            authService.register(username, password, fullName, birthYear, gender, "RECEPTIONIST");
            MessageUtil.setSuccess(session, "Account created successfully! Please log in below.");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return null;
        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
            request.setAttribute("fullName", fullName);
            request.setAttribute("username", username);
            request.setAttribute("birthYear", birthYear);
            request.setAttribute("gender", gender);
            return "register.jsp";
        } catch (Exception e) {
            MessageUtil.setError(session, "Unexpected error: could not create the account.");
            return "register.jsp";
        }
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.trim().isEmpty()) ? null : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}

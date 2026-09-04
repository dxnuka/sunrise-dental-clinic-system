package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AuthService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class UpdateProfileHandler implements RequestHandler {
    private final AuthService authService = new AuthService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User current = (User) session.getAttribute("loggedInUser");
        String fullName = request.getParameter("fullName");
        String gender = request.getParameter("gender");
        Integer birthYear = parseIntOrNull(request.getParameter("birthYear"));

        try {
            authService.updateProfile(current.getUserId(), fullName, birthYear, gender);
            // Refresh the session copy so the header/name updates immediately.
            User updated = authService.findById(current.getUserId());
            session.setAttribute("loggedInUser", updated);
            MessageUtil.setSuccess(session, "Profile updated successfully.");
        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
        } catch (Exception e) {
            MessageUtil.setError(session, "Unexpected error: could not update profile.");
        }

        request.setAttribute("profileUser", authService.findById(current.getUserId()));
        return "profile.jsp";
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.trim().isEmpty()) ? null : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}

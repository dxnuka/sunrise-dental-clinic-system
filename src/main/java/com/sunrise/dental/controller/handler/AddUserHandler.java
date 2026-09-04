package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.service.AuthService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Handles submission of the admin-only "Add User" form - can create either
 *  a RECEPTIONIST or an ADMIN account. Access is restricted to ADMIN role by
 *  FrontControllerServlet's ADMIN_ONLY_ACTIONS check before this ever runs. */
public class AddUserHandler implements RequestHandler {
    private final AuthService authService = new AuthService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String username = request.getParameter("newUsername");
        String password = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("newConfirmPassword");
        String fullName = request.getParameter("newFullName");
        String gender = request.getParameter("newGender");
        String role = request.getParameter("newRole");
        Integer birthYear = parseIntOrNull(request.getParameter("newBirthYear"));

        try {
            if (password != null && !password.equals(confirmPassword)) {
                throw new ValidationException("Password and confirmation do not match.");
            }
            authService.register(username, password, fullName, birthYear, gender, role);
            MessageUtil.setSuccess(session, "New " + (("ADMIN".equals(role)) ? "admin" : "receptionist")
                    + " user \"" + username + "\" created successfully.");
        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
        } catch (Exception e) {
            MessageUtil.setError(session, "Unexpected error: could not create the user.");
        }
        return "add-user.jsp";
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.trim().isEmpty()) ? null : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}

package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.service.AuthService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** Deletes a RECEPTIONIST account (AuthService refuses to delete an ADMIN
 *  account even if this were called with one, as defence in depth beyond the
 *  UI only showing the Delete button on receptionist cards). Access is
 *  restricted to ADMIN role by FrontControllerServlet's ADMIN_ONLY_ACTIONS
 *  check. Redirects back to the Users list with a fresh GET afterward, so a
 *  page refresh never re-submits the delete. */
public class DeleteUserHandler implements RequestHandler {
    private final AuthService authService = new AuthService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            authService.deleteUser(userId);
            MessageUtil.setSuccess(session, "User account deleted.");
        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
        } catch (NumberFormatException nfe) {
            MessageUtil.setWarning(session, "Invalid user reference.");
        } catch (Exception e) {
            MessageUtil.setError(session, "Unexpected error: could not delete the user.");
        }
        response.sendRedirect(request.getContextPath() + "/control?action=users");
        return null;
    }
}

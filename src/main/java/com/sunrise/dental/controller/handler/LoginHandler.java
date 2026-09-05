package com.sunrise.dental.controller.handler;

import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AuthService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class LoginHandler implements RequestHandler {
    private final AuthService authService = new AuthService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = authService.login(username, password);
        HttpSession session = request.getSession();

        if (user == null) {
            MessageUtil.setError(session, "Invalid username or password. Please try again.");
            return "index.jsp";
        }

        session.setAttribute("loggedInUser", user);
        MessageUtil.setSuccess(session, "Welcome back, " + user.getFullName() + "!");

        response.sendRedirect(request.getContextPath() + "/control?action=dashboard");
        return null;
    }
}

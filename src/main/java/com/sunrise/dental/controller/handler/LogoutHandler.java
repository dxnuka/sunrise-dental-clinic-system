package com.sunrise.dental.controller.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogoutHandler implements RequestHandler {
    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate(); // clears the login session entirely
        return "index.jsp";
    }
}

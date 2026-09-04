package com.sunrise.dental.controller.handler;

import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ProfileHandler implements RequestHandler {
    private final AuthService authService = new AuthService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        User current = (User) request.getSession().getAttribute("loggedInUser");
        User fresh = authService.findById(current.getUserId()); // reload latest saved values
        request.setAttribute("profileUser", fresh != null ? fresh : current);
        return "profile.jsp";
    }
}

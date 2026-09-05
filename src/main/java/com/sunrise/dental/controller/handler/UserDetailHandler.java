package com.sunrise.dental.controller.handler;

import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.service.AuthService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class UserDetailHandler implements RequestHandler {
    private final AuthService authService = new AuthService();
    private final AppointmentService appointmentService = new AppointmentService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            User user = authService.findById(userId);
            if (user == null) {
                MessageUtil.setWarning(request.getSession(), "User not found.");
            } else {
                request.setAttribute("viewedUser", user);
                request.setAttribute("createdAppointments", appointmentService.findCreatedByUser(userId));
            }
        } catch (NumberFormatException nfe) {
            MessageUtil.setWarning(request.getSession(), "Invalid user reference.");
        }
        return "user-detail.jsp";
    }
}

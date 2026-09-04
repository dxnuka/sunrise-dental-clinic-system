package com.sunrise.dental.controller.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Shows the admin-only "Add User" page. Access is restricted to ADMIN role
 *  by FrontControllerServlet's ADMIN_ONLY_ACTIONS check before this ever runs. */
public class AddUserPageHandler implements RequestHandler {
    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        return "add-user.jsp";
    }
}

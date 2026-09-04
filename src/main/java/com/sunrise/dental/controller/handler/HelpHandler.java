package com.sunrise.dental.controller.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HelpHandler implements RequestHandler {
    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        return "help.jsp";
    }
}

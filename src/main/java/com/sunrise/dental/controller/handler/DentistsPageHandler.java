package com.sunrise.dental.controller.handler;

import com.sunrise.dental.service.DentistService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DentistsPageHandler implements RequestHandler {
    private final DentistService dentistService = new DentistService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("dentists", dentistService.findAll());
        return "dentists.jsp";
    }
}

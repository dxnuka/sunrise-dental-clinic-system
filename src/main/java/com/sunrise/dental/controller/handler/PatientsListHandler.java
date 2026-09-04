package com.sunrise.dental.controller.handler;

import com.sunrise.dental.dao.PatientFilter;
import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.service.PatientService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PatientsListHandler implements RequestHandler {
    private final PatientService patientService = new PatientService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        PatientFilter filter = new PatientFilter();
        filter.setSearchTerm(request.getParameter("q"));
        filter.setSortField(defaultIfBlank(request.getParameter("sort"), "name"));
        filter.setSortDir(defaultIfBlank(request.getParameter("dir"), "asc"));
        filter.setPage(parseIntOrDefault(request.getParameter("page"), 1));
        filter.setPageSize(9);

        PageResult<Patient> page = patientService.findPaged(filter);
        request.setAttribute("patientsPage", page);
        request.setAttribute("filter", filter);
        return "patients.jsp";
    }

    private int parseIntOrDefault(String s, int def) {
        try { return (s == null || s.trim().isEmpty()) ? def : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }
    private String defaultIfBlank(String s, String def) { return (s == null || s.trim().isEmpty()) ? def : s.trim(); }
}

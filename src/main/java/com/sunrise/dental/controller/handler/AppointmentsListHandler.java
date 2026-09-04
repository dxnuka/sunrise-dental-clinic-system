package com.sunrise.dental.controller.handler;

import com.sunrise.dental.dao.AppointmentFilter;
import com.sunrise.dental.factory.DAOFactory;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.service.AppointmentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Backs the dashboard: a searchable / filterable / sortable / paginated
 *  card-view list of every appointment. */
public class AppointmentsListHandler implements RequestHandler {
    private final AppointmentService appointmentService = new AppointmentService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        AppointmentFilter filter = new AppointmentFilter();
        filter.setSearchTerm(request.getParameter("q"));
        filter.setDentistId(parseIntOrNull(request.getParameter("dentistId")));
        filter.setTreatmentId(parseIntOrNull(request.getParameter("treatmentId")));
        filter.setStatus(emptyToNull(request.getParameter("status")));
        filter.setSortField(defaultIfBlank(request.getParameter("sort"), "date"));
        filter.setSortDir(defaultIfBlank(request.getParameter("dir"), "asc"));
        filter.setPage(parseIntOrDefault(request.getParameter("page"), 1));
        filter.setPageSize(9);

        PageResult<Appointment> page = appointmentService.findPaged(filter);

        request.setAttribute("appointmentsPage", page);
        request.setAttribute("filter", filter);
        request.setAttribute("dentists", DAOFactory.getDentistDAO().findAllActive());
        request.setAttribute("treatments", DAOFactory.getTreatmentDAO().findAll());
        return "dashboard.jsp";
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.trim().isEmpty()) ? null : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }
    private int parseIntOrDefault(String s, int def) {
        try { return (s == null || s.trim().isEmpty()) ? def : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }
    private String emptyToNull(String s) { return (s == null || s.trim().isEmpty()) ? null : s.trim(); }
    private String defaultIfBlank(String s, String def) { return (s == null || s.trim().isEmpty()) ? def : s.trim(); }
}

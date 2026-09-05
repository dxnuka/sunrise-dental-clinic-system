package com.sunrise.dental.controller.handler;

import com.sunrise.dental.service.ReportService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public class ReportsHandler implements RequestHandler {
    private final ReportService reportService = new ReportService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        LocalDate revenueFrom = parseOrDefault(request.getParameter("revenueFrom"), ReportService.defaultFrom());
        LocalDate revenueTo   = parseOrDefault(request.getParameter("revenueTo"), ReportService.defaultTo());
        LocalDate workloadFrom = parseOrDefault(request.getParameter("workloadFrom"), ReportService.defaultFrom());
        LocalDate workloadTo   = parseOrDefault(request.getParameter("workloadTo"), ReportService.defaultTo());
        LocalDate outstandingFrom = parseOrDefault(request.getParameter("outstandingFrom"), ReportService.defaultFrom());
        LocalDate outstandingTo   = parseOrDefault(request.getParameter("outstandingTo"), ReportService.defaultTo());

        request.setAttribute("revenueByTreatment", reportService.revenueByTreatment(revenueFrom, revenueTo));
        request.setAttribute("dentistWorkload", reportService.dentistWorkload(workloadFrom, workloadTo));
        request.setAttribute("outstandingBills", reportService.outstandingBills(outstandingFrom, outstandingTo));

        request.setAttribute("revenueFrom", revenueFrom);
        request.setAttribute("revenueTo", revenueTo);
        request.setAttribute("workloadFrom", workloadFrom);
        request.setAttribute("workloadTo", workloadTo);
        request.setAttribute("outstandingFrom", outstandingFrom);
        request.setAttribute("outstandingTo", outstandingTo);
        return "reports.jsp";
    }

    private LocalDate parseOrDefault(String s, LocalDate def) {
        try { return (s == null || s.trim().isEmpty()) ? def : LocalDate.parse(s.trim()); }
        catch (Exception e) { return def; }
    }
}

package com.sunrise.dental.controller.handler;

import com.sunrise.dental.service.ReportService;
import com.sunrise.dental.util.PdfReportGenerator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenerateReportPdfHandler implements RequestHandler {
    private final ReportService reportService = new ReportService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String reportType = request.getParameter("reportType");
        LocalDate from = parseOrDefault(request.getParameter("from"), ReportService.defaultFrom());
        LocalDate to = parseOrDefault(request.getParameter("to"), ReportService.defaultTo());

        String title;
        List<String> headers;
        List<List<String>> rows = new ArrayList<>();

        if ("workload".equals(reportType)) {
            title = "Dentist Workload Report";
            headers = List.of("Dentist", "Total Appointments", "Completed");
            for (Map<String, Object> r : reportService.dentistWorkload(from, to)) {
                rows.add(List.of(str(r.get("dentist_name")), str(r.get("total_appointments")), str(r.get("completed"))));
            }
        } else if ("outstanding".equals(reportType)) {
            title = "Outstanding Bills Report";
            headers = List.of("Bill No", "Appointment No", "Patient ID", "Patient", "Amount (LKR)");
            for (Map<String, Object> r : reportService.outstandingBills(from, to)) {
                rows.add(List.of(str(r.get("bill_id")), str(r.get("appointment_number")),
                        str(r.get("patient_id")), str(r.get("patient_name")), str(r.get("total_amount"))));
            }
        } else {
            title = "Revenue by Treatment Report";
            headers = List.of("Treatment", "Bills Issued", "Total Revenue (LKR)");
            for (Map<String, Object> r : reportService.revenueByTreatment(from, to)) {
                rows.add(List.of(str(r.get("treatment_name")), str(r.get("bills_issued")), str(r.get("total_revenue"))));
            }
        }

        byte[] pdfBytes = PdfReportGenerator.generate(title, from, to, headers, rows);

        response.setContentType("application/pdf");
        response.setContentLength(pdfBytes.length);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + reportType + "-report.pdf\"");
        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfBytes);
            os.flush();
        }
        return null;
    }

    private String str(Object o) { return o == null ? "-" : o.toString(); }

    private LocalDate parseOrDefault(String s, LocalDate def) {
        try { return (s == null || s.trim().isEmpty()) ? def : LocalDate.parse(s.trim()); }
        catch (Exception e) { return def; }
    }
}

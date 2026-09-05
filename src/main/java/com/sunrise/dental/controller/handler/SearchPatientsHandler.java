package com.sunrise.dental.controller.handler;

import com.sunrise.dental.model.Patient;
import com.sunrise.dental.service.PatientService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SearchPatientsHandler implements RequestHandler {
    private final PatientService patientService = new PatientService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String q = request.getParameter("q");
        List<Patient> matches = patientService.search(q);

        response.setContentType("application/json;charset=UTF-8");
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < matches.size(); i++) {
            Patient p = matches.get(i);
            if (i > 0) json.append(",");
            json.append("{\"id\":").append(p.getPatientId())
                .append(",\"name\":\"").append(escape(p.getPatientName())).append("\"")
                .append(",\"contact\":\"").append(escape(p.getContactNumber())).append("\"")
                .append(",\"address\":\"").append(escape(p.getAddress())).append("\"}");
        }
        json.append("]");
        response.getWriter().write(json.toString());
        return null;
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

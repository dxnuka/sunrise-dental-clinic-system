package com.sunrise.dental.controller.handler;

import com.sunrise.dental.factory.DAOFactory;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.service.AppointmentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AvailableSlotsHandler implements RequestHandler {
    private final AppointmentService appointmentService = new AppointmentService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int dentistId = Integer.parseInt(request.getParameter("dentistId"));
            int treatmentId = Integer.parseInt(request.getParameter("treatmentId"));
            LocalDate date = LocalDate.parse(request.getParameter("date"));

            Treatment treatment = DAOFactory.getTreatmentDAO().findById(treatmentId);
            int candidateDuration = (treatment != null) ? treatment.getDurationMinutes() : 30;

            List<Appointment> existing = appointmentService.findScheduledForDentistOnDate(dentistId, date);
            List<int[]> busyIntervals = new ArrayList<>();
            for (Appointment a : existing) {
                int start = toMinutes(a.getAppointmentTime());
                int end = start + a.getTreatment().getDurationMinutes();
                busyIntervals.add(new int[]{start, end});
            }

            boolean isToday = date.equals(LocalDate.now());
            int nowMinutes = isToday ? toMinutes(LocalTime.now()) : -1;

            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (int minutes = 8 * 60; minutes <= 17 * 60; minutes += 15) {
                int candidateEnd = minutes + candidateDuration;
                boolean available = true;
                for (int[] busy : busyIntervals) {
                    if (minutes < busy[1] && busy[0] < candidateEnd) { available = false; break; }
                }
                if (isToday && minutes <= nowMinutes) available = false; // already passed today

                if (!first) json.append(",");
                first = false;
                json.append("{\"time\":\"").append(formatTime(minutes)).append("\",\"available\":").append(available).append("}");
            }
            json.append("]");
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            response.getWriter().write("[]");
        }
        return null;
    }

    private int toMinutes(LocalTime t) { return t.getHour() * 60 + t.getMinute(); }

    private String formatTime(int minutesSinceMidnight) {
        int h = minutesSinceMidnight / 60;
        int m = minutesSinceMidnight % 60;
        return String.format("%02d:%02d", h, m);
    }
}

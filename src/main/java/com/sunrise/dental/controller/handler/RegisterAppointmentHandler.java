package com.sunrise.dental.controller.handler;

import com.sunrise.dental.exception.DoubleBookingException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.factory.DAOFactory;
import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;

public class RegisterAppointmentHandler implements RequestHandler {
    private final AppointmentService appointmentService = new AppointmentService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User staff = (User) session.getAttribute("loggedInUser");
        String patientMode = request.getParameter("patientMode"); // "new" or "existing"

        try {
            int dentistId = parseInt(request.getParameter("dentistId"));
            int treatmentId = parseInt(request.getParameter("treatmentId"));
            LocalDate date = LocalDate.parse(request.getParameter("appointmentDate"));
            LocalTime time = LocalTime.parse(request.getParameter("appointmentTime"));

            String appointmentNumber;
            if ("existing".equals(patientMode)) {
                int patientId = parseInt(request.getParameter("existingPatientId"));
                appointmentNumber = appointmentService.registerForExistingPatient(
                        patientId, dentistId, treatmentId, date, time, staff.getUserId());
            } else {
                String name = request.getParameter("patientName");
                String address = request.getParameter("address");
                String contact = request.getParameter("contactNumber");
                Integer birthYear = parseIntOrNull(request.getParameter("patientBirthYear"));
                String gender = request.getParameter("patientGender");
                appointmentNumber = appointmentService.registerForNewPatient(
                        name, address, contact, birthYear, gender, dentistId, treatmentId, date, time, staff.getUserId());
            }

            MessageUtil.setSuccess(session,
                    "Appointment registered successfully! Appointment number: " + appointmentNumber);

        } catch (ValidationException ve) {
            MessageUtil.setWarning(session, ve.getMessage());
        } catch (DoubleBookingException dbe) {
            MessageUtil.setError(session, dbe.getMessage());
        } catch (NumberFormatException nfe) {
            MessageUtil.setWarning(session, "Please select a dentist, treatment, and existing patient (if applicable).");
        } catch (Exception e) {
            MessageUtil.setError(session, "Unexpected error: could not register the appointment.");
        }

        request.setAttribute("dentists", DAOFactory.getDentistDAO().findAllActive());
        request.setAttribute("treatments", DAOFactory.getTreatmentDAO().findAll());
        return "add-appointment.jsp";
    }

    private int parseInt(String s) {
        if (s == null || s.trim().isEmpty()) throw new NumberFormatException("blank");
        return Integer.parseInt(s.trim());
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.trim().isEmpty()) ? null : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}

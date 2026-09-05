package com.sunrise.dental.service;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.dao.PatientDAO;
import com.sunrise.dental.dao.PatientFilter;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.factory.DAOFactory;
import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.model.PatientSummary;
import com.sunrise.dental.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class PatientService {

    private final PatientDAO patientDAO;
    private final AppointmentDAO appointmentDAO;

    public PatientService() { this(DAOFactory.getPatientDAO(), DAOFactory.getAppointmentDAO()); }
    public PatientService(PatientDAO patientDAO, AppointmentDAO appointmentDAO) {
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
    }

    public List<Patient> search(String query) {
        if (query == null || query.trim().length() < 2) return new ArrayList<>();
        return patientDAO.search(query.trim(), 10);
    }

    public PageResult<Patient> findPaged(PatientFilter filter) {
        return patientDAO.findPaged(filter);
    }

    public PatientSummary getSummary(int patientId) throws ValidationException {
        Patient patient = patientDAO.findById(patientId);
        if (patient == null) throw new ValidationException("Patient not found.");

        PatientSummary summary = new PatientSummary();
        summary.setPatient(patient);
        summary.setTotalAppointments(appointmentDAO.countForPatient(patientId));
        summary.setLastAppointment(appointmentDAO.findLastAppointmentForPatient(patientId));
        summary.setNextAppointment(appointmentDAO.findNextAppointmentForPatient(patientId));
        return summary;
    }

    public void deletePatient(int patientId) throws ValidationException {
        Patient patient = patientDAO.findById(patientId);
        if (patient == null) throw new ValidationException("Patient not found.");
        int appointmentCount = appointmentDAO.countForPatient(patientId);
        if (appointmentCount > 0) {
            throw new ValidationException(
                "This patient has " + appointmentCount + " appointment(s) on record and cannot be deleted. " +
                "Patients with appointment history are kept to preserve those records.");
        }
        patientDAO.delete(patientId);
    }
}

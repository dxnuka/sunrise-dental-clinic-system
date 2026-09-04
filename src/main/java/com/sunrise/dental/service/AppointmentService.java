package com.sunrise.dental.service;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.dao.AppointmentFilter;
import com.sunrise.dental.dao.TreatmentDAO;
import com.sunrise.dental.exception.DoubleBookingException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.factory.DAOFactory;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.observer.AppointmentEventPublisher;
import com.sunrise.dental.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Business Logic Tier for appointments. Depends only on AppointmentDAO/
 * TreatmentDAO interfaces (obtained via the DAO Factory), which is what lets
 * AppointmentServiceTest mock those DAOs and test validation/double-booking
 * rules with no real database - see docs/testing-and-tdd.md.
 */
public class AppointmentService {

    private static final List<String> ALLOWED_STATUSES = List.of("SCHEDULED", "COMPLETED", "CANCELLED");

    private final AppointmentDAO appointmentDAO;
    private final TreatmentDAO treatmentDAO;

    public AppointmentService() {
        this(DAOFactory.getAppointmentDAO(), DAOFactory.getTreatmentDAO());
    }

    /** Constructor-injection overload used by unit tests to pass in mock DAOs. */
    public AppointmentService(AppointmentDAO appointmentDAO, TreatmentDAO treatmentDAO) {
        this.appointmentDAO = appointmentDAO;
        this.treatmentDAO = treatmentDAO;
    }

    /** Registers a brand-new patient (with demographics) plus their appointment. */
    public String registerForNewPatient(String patientName, String address, String contact,
                                         Integer birthYear, String gender,
                                         int dentistId, int treatmentId,
                                         LocalDate date, LocalTime time, int staffUserId)
            throws ValidationException, DoubleBookingException {

        ValidationUtil.requireValidName(patientName, "Patient name");
        ValidationUtil.requireValidAddress(address);
        ValidationUtil.requireValidPhone(contact);
        ValidationUtil.requireValidBirthYear(birthYear);
        ValidationUtil.requireValidGender(gender);

        validateScheduleAndGetDuration(dentistId, treatmentId, date, time);

        Patient patient = new Patient(patientName.trim(), address.trim(), contact.trim(), birthYear, gender);
        String appointmentNumber = appointmentDAO.register(patient, dentistId, treatmentId, date, time, staffUserId);
        publishRegisteredEvent(appointmentNumber);
        return appointmentNumber;
    }

    /** Books an appointment for a patient who already exists in the system. */
    public String registerForExistingPatient(int patientId, int dentistId, int treatmentId,
                                              LocalDate date, LocalTime time, int staffUserId)
            throws ValidationException, DoubleBookingException {

        ValidationUtil.requirePositiveId(patientId, "patient");
        validateScheduleAndGetDuration(dentistId, treatmentId, date, time);

        String appointmentNumber = appointmentDAO.registerExisting(patientId, dentistId, treatmentId, date, time, staffUserId);
        publishRegisteredEvent(appointmentNumber);
        return appointmentNumber;
    }

    /** Shared validation: dentist/treatment selected, date/time rules, and the
     *  overlap-aware double-booking guard (defence in depth alongside the DB trigger). */
    private void validateScheduleAndGetDuration(int dentistId, int treatmentId, LocalDate date, LocalTime time)
            throws ValidationException, DoubleBookingException {

        ValidationUtil.requirePositiveId(dentistId, "dentist");
        ValidationUtil.requirePositiveId(treatmentId, "treatment");
        ValidationUtil.requireFutureOrTodayDate(date);
        ValidationUtil.requireValidTimeSlot(time);
        ValidationUtil.requireNotPastDateTime(date, time);

        Treatment treatment = treatmentDAO.findById(treatmentId);
        if (treatment == null) throw new ValidationException("Selected treatment could not be found.");

        if (appointmentDAO.isSlotTaken(dentistId, date, time, treatment.getDurationMinutes())) {
            throw new DoubleBookingException(
                "This dentist already has an overlapping appointment at that date and time. Please choose another slot.");
        }
    }

    private void publishRegisteredEvent(String appointmentNumber) {
        Appointment created = appointmentDAO.findByAppointmentNumber(appointmentNumber);
        if (created != null) AppointmentEventPublisher.publishAppointmentRegistered(created);
    }

    public Appointment find(String appointmentNumber) throws ValidationException {
        ValidationUtil.requireNonBlank(appointmentNumber, "Appointment number");
        return appointmentDAO.findByAppointmentNumber(appointmentNumber);
    }

    public PageResult<Appointment> findPaged(AppointmentFilter filter) {
        return appointmentDAO.findPaged(filter);
    }

    /** Returns, for every 15-minute slot in clinic hours, whether it is free for this
     *  dentist+treatment+date combination - powers the grey-out-unavailable-slots UI. */
    public List<Appointment> findScheduledForDentistOnDate(int dentistId, LocalDate date) {
        return appointmentDAO.findScheduledForDentistOnDate(dentistId, date);
    }

    /** Every appointment a given staff member registered - backs the admin-only user detail view. */
    public List<Appointment> findCreatedByUser(int userId) {
        return appointmentDAO.findCreatedByUser(userId);
    }

    /** Changes an appointment's status (e.g. SCHEDULED -> COMPLETED or CANCELLED). */
    public void updateStatus(String appointmentNumber, String newStatus) throws ValidationException {
        ValidationUtil.requireNonBlank(appointmentNumber, "Appointment number");
        if (newStatus == null || !ALLOWED_STATUSES.contains(newStatus)) {
            throw new ValidationException("Please choose a valid status.");
        }
        Appointment appointment = appointmentDAO.findByAppointmentNumber(appointmentNumber);
        if (appointment == null) throw new ValidationException("Appointment not found.");
        appointmentDAO.updateStatus(appointment.getAppointmentId(), newStatus);
    }
}

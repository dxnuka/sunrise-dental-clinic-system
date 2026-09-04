package com.sunrise.dental.dao;

import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.exception.DoubleBookingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentDAO {

    /** Registers a NEW patient (with demographics) + appointment via sp_register_appointment. */
    String register(Patient patient, int dentistId, int treatmentId,
                     LocalDate date, LocalTime time, int createdByUserId) throws DoubleBookingException;

    /** Books an appointment for an EXISTING patient via sp_register_appointment_existing. */
    String registerExisting(int patientId, int dentistId, int treatmentId,
                             LocalDate date, LocalTime time, int createdByUserId) throws DoubleBookingException;

    Appointment findByAppointmentNumber(String appointmentNumber);

    /** Overlap-aware slot check: true if [time, time+durationMinutes) clashes with an
     *  existing SCHEDULED appointment for this dentist on this date. */
    boolean isSlotTaken(int dentistId, LocalDate date, LocalTime time, int durationMinutes);

    /** Every SCHEDULED appointment for one dentist on one date - used to compute which
     *  candidate time slots to grey out on the add-appointment page before the user
     *  ever submits, rather than relying only on the reject-on-submit error path. */
    List<Appointment> findScheduledForDentistOnDate(int dentistId, LocalDate date);

    List<Appointment> findDailySchedule(LocalDate date);

    /** Backs the dashboard's searchable/filterable/sortable/paginated appointment list. */
    PageResult<Appointment> findPaged(AppointmentFilter filter);

    int countForPatient(int patientId);
    Appointment findLastAppointmentForPatient(int patientId);
    Appointment findNextAppointmentForPatient(int patientId);

    void updateStatus(int appointmentId, String newStatus);

    /** Every appointment a given staff member registered - backs the admin-only
     *  user detail view (Manage Users -> click a user card). */
    List<Appointment> findCreatedByUser(int userId);
}

package com.sunrise.dental.dao;

import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.exception.DoubleBookingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentDAO {

    String register(Patient patient, int dentistId, int treatmentId,
                     LocalDate date, LocalTime time, int createdByUserId) throws DoubleBookingException;

    String registerExisting(int patientId, int dentistId, int treatmentId,
                             LocalDate date, LocalTime time, int createdByUserId) throws DoubleBookingException;

    Appointment findByAppointmentNumber(String appointmentNumber);

    boolean isSlotTaken(int dentistId, LocalDate date, LocalTime time, int durationMinutes);

    List<Appointment> findScheduledForDentistOnDate(int dentistId, LocalDate date);

    List<Appointment> findDailySchedule(LocalDate date);

    PageResult<Appointment> findPaged(AppointmentFilter filter);

    int countForPatient(int patientId);
    Appointment findLastAppointmentForPatient(int patientId);
    Appointment findNextAppointmentForPatient(int patientId);

    void updateStatus(int appointmentId, String newStatus);

    List<Appointment> findCreatedByUser(int userId);
}

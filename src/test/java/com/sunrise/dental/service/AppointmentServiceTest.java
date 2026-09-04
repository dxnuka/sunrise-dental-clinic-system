package com.sunrise.dental.service;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.dao.TreatmentDAO;
import com.sunrise.dental.exception.DoubleBookingException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Treatment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ---------------------------------------------------------------------------
 * TEST PLAN - AppointmentService
 * ---------------------------------------------------------------------------
 * Both AppointmentDAO and TreatmentDAO are mocked with Mockito, so these are
 * true *unit* tests - no real database is required to run them. The
 * double-booking and validation rules are tested first since the scenario
 * names "double bookings" as the clinic's biggest current problem, and
 * "restrict invalid entries" is an explicit brief requirement.
 * ---------------------------------------------------------------------------
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentDAO appointmentDAO;
    @Mock private TreatmentDAO treatmentDAO;

    private AppointmentService appointmentService;

    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final LocalTime TEN_AM = LocalTime.of(10, 0);
    private static final int ROOT_CANAL_DURATION = 90;
    private static final Integer VALID_BIRTH_YEAR = 1990;
    private static final String VALID_GENDER = "MALE";

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(appointmentDAO, treatmentDAO);
    }

    private Treatment sampleTreatment() {
        return new Treatment(1, "Root Canal Treatment", new BigDecimal("15000.00"), ROOT_CANAL_DURATION);
    }

    // ------------------------------------------------------------------
    // New-patient registration
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Rejects a new-patient booking when the dentist has an overlapping SCHEDULED appointment")
    void registerForNewPatient_rejectsDoubleBooking() throws Exception {
        when(treatmentDAO.findById(1)).thenReturn(sampleTreatment());
        when(appointmentDAO.isSlotTaken(1, TOMORROW, TEN_AM, ROOT_CANAL_DURATION)).thenReturn(true);

        DoubleBookingException ex = assertThrows(DoubleBookingException.class, () ->
                appointmentService.registerForNewPatient("Nimal Perera", "12 Galle Rd, Colombo",
                        "0771234567", VALID_BIRTH_YEAR, VALID_GENDER, 1, 1, TOMORROW, TEN_AM, 1));

        assertTrue(ex.getMessage().toLowerCase().contains("overlapping"));
        verify(appointmentDAO, never()).register(any(), anyInt(), anyInt(), any(), any(), anyInt());
    }

    @Test
    @DisplayName("Accepts a new-patient booking and returns the generated appointment number when the slot is free")
    void registerForNewPatient_success() throws Exception {
        when(treatmentDAO.findById(1)).thenReturn(sampleTreatment());
        when(appointmentDAO.isSlotTaken(1, TOMORROW, TEN_AM, ROOT_CANAL_DURATION)).thenReturn(false);
        when(appointmentDAO.register(any(), eq(1), eq(1), eq(TOMORROW), eq(TEN_AM), eq(1)))
                .thenReturn("APT00001");
        when(appointmentDAO.findByAppointmentNumber("APT00001")).thenReturn(new Appointment());

        String result = appointmentService.registerForNewPatient("Nimal Perera", "12 Galle Rd, Colombo",
                "0771234567", VALID_BIRTH_YEAR, VALID_GENDER, 1, 1, TOMORROW, TEN_AM, 1);

        assertEquals("APT00001", result);
    }

    @Test
    @DisplayName("Rejects a blank patient name before ever calling a DAO")
    void registerForNewPatient_rejectsBlankName() {
        assertThrows(ValidationException.class, () ->
                appointmentService.registerForNewPatient("   ", "12 Galle Rd", "0771234567",
                        VALID_BIRTH_YEAR, VALID_GENDER, 1, 1, TOMORROW, TEN_AM, 1));
        verifyNoInteractions(appointmentDAO, treatmentDAO);
    }

    @Test
    @DisplayName("Rejects a patient name containing digits or symbols")
    void registerForNewPatient_rejectsNameWithDigits() {
        assertThrows(ValidationException.class, () ->
                appointmentService.registerForNewPatient("Nimal123", "12 Galle Rd", "0771234567",
                        VALID_BIRTH_YEAR, VALID_GENDER, 1, 1, TOMORROW, TEN_AM, 1));
    }

    @Test
    @DisplayName("Rejects a contact number that isn't exactly 10 digits")
    void registerForNewPatient_rejectsInvalidPhone() {
        assertThrows(ValidationException.class, () ->
                appointmentService.registerForNewPatient("Nimal Perera", "12 Galle Rd", "077123",
                        VALID_BIRTH_YEAR, VALID_GENDER, 1, 1, TOMORROW, TEN_AM, 1));
    }

    @Test
    @DisplayName("Rejects a birth year in the future")
    void registerForNewPatient_rejectsFutureBirthYear() {
        int nextYear = Year.now().getValue() + 1;
        assertThrows(ValidationException.class, () ->
                appointmentService.registerForNewPatient("Nimal Perera", "12 Galle Rd", "0771234567",
                        nextYear, VALID_GENDER, 1, 1, TOMORROW, TEN_AM, 1));
    }

    @Test
    @DisplayName("Rejects a missing/invalid gender value")
    void registerForNewPatient_rejectsInvalidGender() {
        assertThrows(ValidationException.class, () ->
                appointmentService.registerForNewPatient("Nimal Perera", "12 Galle Rd", "0771234567",
                        VALID_BIRTH_YEAR, "UNKNOWN", 1, 1, TOMORROW, TEN_AM, 1));
    }

    @Test
    @DisplayName("Rejects a date in the past")
    void registerForNewPatient_rejectsPastDate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertThrows(ValidationException.class, () ->
                appointmentService.registerForNewPatient("Nimal Perera", "12 Galle Rd", "0771234567",
                        VALID_BIRTH_YEAR, VALID_GENDER, 1, 1, yesterday, TEN_AM, 1));
    }

    @Test
    @DisplayName("Rejects a time outside clinic hours (08:00-17:00)")
    void registerForNewPatient_rejectsOutOfHours() {
        assertThrows(ValidationException.class, () ->
                appointmentService.registerForNewPatient("Nimal Perera", "12 Galle Rd", "0771234567",
                        VALID_BIRTH_YEAR, VALID_GENDER, 1, 1, TOMORROW, LocalTime.of(20, 30), 1));
    }

    @Test
    @DisplayName("Rejects a time that isn't on a 15-minute block")
    void registerForNewPatient_rejectsNonQuarterHourSlot() {
        assertThrows(ValidationException.class, () ->
                appointmentService.registerForNewPatient("Nimal Perera", "12 Galle Rd", "0771234567",
                        VALID_BIRTH_YEAR, VALID_GENDER, 1, 1, TOMORROW, LocalTime.of(10, 7), 1));
    }

    @Test
    @DisplayName("Accepts a time on a 15-minute block that isn't also a 30-minute one")
    void registerForNewPatient_acceptsQuarterHourSlot() throws Exception {
        LocalTime quarterPast = LocalTime.of(10, 15);
        when(treatmentDAO.findById(1)).thenReturn(sampleTreatment());
        when(appointmentDAO.isSlotTaken(1, TOMORROW, quarterPast, ROOT_CANAL_DURATION)).thenReturn(false);
        when(appointmentDAO.register(any(), eq(1), eq(1), eq(TOMORROW), eq(quarterPast), eq(1)))
                .thenReturn("APT00003");
        when(appointmentDAO.findByAppointmentNumber("APT00003")).thenReturn(new Appointment());

        String result = appointmentService.registerForNewPatient("Nimal Perera", "12 Galle Rd, Colombo",
                "0771234567", VALID_BIRTH_YEAR, VALID_GENDER, 1, 1, TOMORROW, quarterPast, 1);

        assertEquals("APT00003", result);
    }

    @Test
    @DisplayName("Rejects today's date with a time that has already passed")
    void registerForNewPatient_rejectsPastTimeToday() {
        LocalTime aMinuteAgo = LocalTime.now().minusMinutes(1).withSecond(0).withNano(0);
        // Round down to the current or previous 15-minute mark so it's still a "valid"
        // slot shape, guaranteeing it is in the past relative to LocalTime.now().
        int roundedMinute = (aMinuteAgo.getMinute() / 15) * 15;
        LocalTime pastSlot = aMinuteAgo.withMinute(roundedMinute);
        assertThrows(ValidationException.class, () ->
                appointmentService.registerForNewPatient("Nimal Perera", "12 Galle Rd", "0771234567",
                        VALID_BIRTH_YEAR, VALID_GENDER, 1, 1, LocalDate.now(), pastSlot, 1));
    }

    // ------------------------------------------------------------------
    // Existing-patient registration
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Accepts an existing-patient booking and returns the generated appointment number")
    void registerForExistingPatient_success() throws Exception {
        when(treatmentDAO.findById(1)).thenReturn(sampleTreatment());
        when(appointmentDAO.isSlotTaken(1, TOMORROW, TEN_AM, ROOT_CANAL_DURATION)).thenReturn(false);
        when(appointmentDAO.registerExisting(eq(42), eq(1), eq(1), eq(TOMORROW), eq(TEN_AM), eq(1)))
                .thenReturn("APT00002");
        when(appointmentDAO.findByAppointmentNumber("APT00002")).thenReturn(new Appointment());

        String result = appointmentService.registerForExistingPatient(42, 1, 1, TOMORROW, TEN_AM, 1);

        assertEquals("APT00002", result);
    }

    @Test
    @DisplayName("Rejects an existing-patient booking with an invalid (non-positive) patient id")
    void registerForExistingPatient_rejectsInvalidPatientId() {
        assertThrows(ValidationException.class, () ->
                appointmentService.registerForExistingPatient(0, 1, 1, TOMORROW, TEN_AM, 1));
        verifyNoInteractions(appointmentDAO, treatmentDAO);
    }

    @Test
    @DisplayName("Rejects an existing-patient booking when the dentist has an overlapping appointment")
    void registerForExistingPatient_rejectsDoubleBooking() {
        when(treatmentDAO.findById(1)).thenReturn(sampleTreatment());
        when(appointmentDAO.isSlotTaken(1, TOMORROW, TEN_AM, ROOT_CANAL_DURATION)).thenReturn(true);

        assertThrows(DoubleBookingException.class, () ->
                appointmentService.registerForExistingPatient(42, 1, 1, TOMORROW, TEN_AM, 1));
    }

    // ------------------------------------------------------------------
    // Status updates
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Updates an appointment's status when given a valid status and an existing appointment")
    void updateStatus_success() throws Exception {
        Appointment existing = new Appointment();
        existing.setAppointmentId(7);
        when(appointmentDAO.findByAppointmentNumber("APT00007")).thenReturn(existing);

        appointmentService.updateStatus("APT00007", "COMPLETED");

        verify(appointmentDAO).updateStatus(7, "COMPLETED");
    }

    @Test
    @DisplayName("Rejects an invalid status value")
    void updateStatus_rejectsInvalidStatus() {
        assertThrows(ValidationException.class, () ->
                appointmentService.updateStatus("APT00007", "IN_PROGRESS"));
        verifyNoInteractions(appointmentDAO);
    }

    @Test
    @DisplayName("Rejects a status change for an appointment number that doesn't exist")
    void updateStatus_rejectsUnknownAppointment() {
        when(appointmentDAO.findByAppointmentNumber("APT99999")).thenReturn(null);
        assertThrows(ValidationException.class, () ->
                appointmentService.updateStatus("APT99999", "CANCELLED"));
    }
}

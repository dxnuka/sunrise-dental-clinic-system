package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.dao.AppointmentFilter;
import com.sunrise.dental.db.DBConnectionManager;
import com.sunrise.dental.exception.DoubleBookingException;
import com.sunrise.dental.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentDAOImpl implements AppointmentDAO {

    private static final String JOIN_SQL =
            "FROM appointments a " +
            "JOIN patients p ON a.patient_id = p.patient_id " +
            "JOIN dentists d ON a.dentist_id = d.dentist_id " +
            "JOIN treatments t ON a.treatment_id = t.treatment_id ";

    private static final String SELECT_SQL =
            "SELECT a.appointment_id, a.appointment_number, a.appointment_date, a.appointment_time, a.status, a.created_by, " +
                    "p.patient_id, p.patient_name, p.address, p.contact_number, p.birth_year, p.gender, " +
                    "d.dentist_id, d.dentist_name, d.specialization, d.consultation_fee, " +
                    "t.treatment_id, t.treatment_name, t.base_fee, t.duration_minutes " +
                    JOIN_SQL;

    private static final Map<String, String> SORT_COLUMNS = new HashMap<>();
    static {
        SORT_COLUMNS.put("date", "a.appointment_date, a.appointment_time");
        SORT_COLUMNS.put("patient", "p.patient_name");
        SORT_COLUMNS.put("dentist", "d.dentist_name");
        SORT_COLUMNS.put("treatment", "t.treatment_name");
    }

    @Override
    public String register(Patient patient, int dentistId, int treatmentId,
                            LocalDate date, LocalTime time, int createdByUserId) throws DoubleBookingException {
        String call = "{CALL sp_register_appointment(?,?,?,?,?,?,?,?,?,?,?)}";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             CallableStatement cs = con.prepareCall(call)) {
            cs.setString(1, patient.getPatientName());
            cs.setString(2, patient.getAddress());
            cs.setString(3, patient.getContactNumber());
            if (patient.getBirthYear() != null) cs.setInt(4, patient.getBirthYear()); else cs.setNull(4, Types.INTEGER);
            cs.setString(5, patient.getGender());
            cs.setInt(6, dentistId);
            cs.setInt(7, treatmentId);
            cs.setDate(8, Date.valueOf(date));
            cs.setTime(9, Time.valueOf(time));
            cs.setInt(10, createdByUserId);
            cs.registerOutParameter(11, Types.VARCHAR);
            cs.execute();
            return cs.getString(11);
        } catch (SQLException e) {
            throw translateOrRethrow(e);
        }
    }

    @Override
    public String registerExisting(int patientId, int dentistId, int treatmentId,
                                    LocalDate date, LocalTime time, int createdByUserId) throws DoubleBookingException {
        String call = "{CALL sp_register_appointment_existing(?,?,?,?,?,?,?)}";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             CallableStatement cs = con.prepareCall(call)) {
            cs.setInt(1, patientId);
            cs.setInt(2, dentistId);
            cs.setInt(3, treatmentId);
            cs.setDate(4, Date.valueOf(date));
            cs.setTime(5, Time.valueOf(time));
            cs.setInt(6, createdByUserId);
            cs.registerOutParameter(7, Types.VARCHAR);
            cs.execute();
            return cs.getString(7);
        } catch (SQLException e) {
            throw translateOrRethrow(e);
        }
    }

    private DoubleBookingException translateOrRethrow(SQLException e) {
        if (e.getMessage() != null && e.getMessage().toLowerCase().contains("double booking")) {
            return new DoubleBookingException(
                "This dentist already has an overlapping appointment at that date and time. Please choose another slot.");
        }
        throw new RuntimeException("Error registering appointment: " + e.getMessage(), e);
    }

    @Override
    public Appointment findByAppointmentNumber(String appointmentNumber) {
        String sql = SELECT_SQL + "WHERE a.appointment_number = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding appointment: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean isSlotTaken(int dentistId, LocalDate date, LocalTime time, int durationMinutes) {
        String sql = "SELECT COUNT(*) FROM appointments a JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "WHERE a.dentist_id = ? AND a.appointment_date = ? AND a.status = 'SCHEDULED' " +
                     "AND a.appointment_time < ADDTIME(?, SEC_TO_TIME(? * 60)) " +
                     "AND ADDTIME(a.appointment_time, SEC_TO_TIME(t.duration_minutes * 60)) > ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dentistId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(time));
            ps.setInt(4, durationMinutes);
            ps.setTime(5, Time.valueOf(time));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking slot: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<Appointment> findScheduledForDentistOnDate(int dentistId, LocalDate date) {
        List<Appointment> list = new ArrayList<>();
        String sql = SELECT_SQL + "WHERE a.dentist_id = ? AND a.appointment_date = ? AND a.status = 'SCHEDULED' " +
                     "ORDER BY a.appointment_time";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dentistId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading dentist's schedule: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Appointment> findDailySchedule(LocalDate date) {
        List<Appointment> list = new ArrayList<>();
        String sql = SELECT_SQL + "WHERE a.appointment_date = ? ORDER BY a.appointment_time";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading schedule: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public PageResult<Appointment> findPaged(AppointmentFilter filter) {
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (filter.getSearchTerm() != null && !filter.getSearchTerm().trim().isEmpty()) {
            where.append(" AND (p.patient_name LIKE ? OR a.appointment_number LIKE ? " +
                         "OR d.dentist_name LIKE ? OR t.treatment_name LIKE ?) ");
            String like = "%" + filter.getSearchTerm().trim() + "%";
            for (int i = 0; i < 4; i++) params.add(like);
        }
        if (filter.getDentistId() != null) {
            where.append(" AND a.dentist_id = ? ");
            params.add(filter.getDentistId());
        }
        if (filter.getTreatmentId() != null) {
            where.append(" AND a.treatment_id = ? ");
            params.add(filter.getTreatmentId());
        }
        if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
            where.append(" AND a.status = ? ");
            params.add(filter.getStatus());
        }
        if (filter.getDateFrom() != null) {
            where.append(" AND a.appointment_date >= ? ");
            params.add(Date.valueOf(filter.getDateFrom()));
        }
        if (filter.getDateTo() != null) {
            where.append(" AND a.appointment_date <= ? ");
            params.add(Date.valueOf(filter.getDateTo()));
        }


        String sortCol = SORT_COLUMNS.getOrDefault(filter.getSortField(), SORT_COLUMNS.get("date"));
        String sortDir = "desc".equalsIgnoreCase(filter.getSortDir()) ? "DESC" : "ASC";

        long total = countWithWhere(where.toString(), params);

        int page = Math.max(1, filter.getPage());
        int pageSize = Math.max(1, filter.getPageSize());
        int offset = (page - 1) * pageSize;

        String sql = SELECT_SQL + where + " ORDER BY " + sortCol + " " + sortDir + " LIMIT ? OFFSET ?";
        List<Appointment> items = new ArrayList<>();
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            for (Object p : params) ps.setObject(idx++, p);
            ps.setInt(idx++, pageSize);
            ps.setInt(idx, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading appointments: " + e.getMessage(), e);
        }
        return new PageResult<>(items, page, pageSize, total);
    }

    private long countWithWhere(String where, List<Object> params) {
        String sql = "SELECT COUNT(*) " + JOIN_SQL + where;
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            for (Object p : params) ps.setObject(idx++, p);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting appointments: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int countForPatient(int patientId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE patient_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting patient appointments: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public Appointment findLastAppointmentForPatient(int patientId) {
        String sql = SELECT_SQL + "WHERE a.patient_id = ? AND " +
                     "TIMESTAMP(a.appointment_date, a.appointment_time) <= NOW() " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC LIMIT 1";
        return findOneWithPatientId(sql, patientId);
    }

    @Override
    public Appointment findNextAppointmentForPatient(int patientId) {
        String sql = SELECT_SQL + "WHERE a.patient_id = ? AND a.status = 'SCHEDULED' AND " +
                     "TIMESTAMP(a.appointment_date, a.appointment_time) > NOW() " +
                     "ORDER BY a.appointment_date ASC, a.appointment_time ASC LIMIT 1";
        return findOneWithPatientId(sql, patientId);
    }

    private Appointment findOneWithPatientId(String sql, int patientId) {
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading patient appointment: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void updateStatus(int appointmentId, String newStatus) {
        String call = "{CALL sp_update_appointment_status(?,?)}";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             CallableStatement cs = con.prepareCall(call)) {
            cs.setInt(1, appointmentId);
            cs.setString(2, newStatus);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating appointment status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appointment> findCreatedByUser(int userId) {
        List<Appointment> list = new ArrayList<>();
        String sql = SELECT_SQL + "WHERE a.created_by = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading user's created appointments: " + e.getMessage(), e);
        }
        return list;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setAppointmentNumber(rs.getString("appointment_number"));
        a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        a.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        a.setStatus(rs.getString("status"));
        int createdBy = rs.getInt("created_by");
        a.setCreatedByUserId(rs.wasNull() ? null : createdBy);

        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setPatientName(rs.getString("patient_name"));
        p.setAddress(rs.getString("address"));
        p.setContactNumber(rs.getString("contact_number"));
        int birthYear = rs.getInt("birth_year");
        p.setBirthYear(rs.wasNull() ? null : birthYear);
        p.setGender(rs.getString("gender"));
        a.setPatient(p);

        a.setDentist(new Dentist(rs.getInt("dentist_id"), rs.getString("dentist_name"),
                rs.getString("specialization"), rs.getBigDecimal("consultation_fee")));
        a.setTreatment(new Treatment(rs.getInt("treatment_id"), rs.getString("treatment_name"),
                rs.getBigDecimal("base_fee"), rs.getInt("duration_minutes")));
        return a;
    }
}

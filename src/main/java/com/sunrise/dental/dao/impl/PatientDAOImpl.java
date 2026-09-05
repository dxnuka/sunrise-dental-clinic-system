package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.PatientDAO;
import com.sunrise.dental.dao.PatientFilter;
import com.sunrise.dental.db.DBConnectionManager;
import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientDAOImpl implements PatientDAO {

    private static final String COLUMNS =
            "patient_id, patient_name, address, contact_number, birth_year, gender";

    private static final Map<String, String> SORT_COLUMNS = new HashMap<>();
    static {
        SORT_COLUMNS.put("name", "patient_name");
        SORT_COLUMNS.put("registered", "registered_on");
    }

    @Override
    public List<Patient> search(String query, int limit) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM patients " +
                     "WHERE patient_name LIKE ? OR contact_number LIKE ? OR CAST(patient_id AS CHAR) LIKE ? " +
                     "ORDER BY patient_name LIMIT ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + query.trim() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setInt(4, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching patients: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Patient findById(int patientId) {
        String sql = "SELECT " + COLUMNS + " FROM patients WHERE patient_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading patient: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public PageResult<Patient> findPaged(PatientFilter filter) {
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (filter.getSearchTerm() != null && !filter.getSearchTerm().trim().isEmpty()) {
            where.append(" AND (patient_name LIKE ? OR contact_number LIKE ? OR address LIKE ? OR CAST(patient_id AS CHAR) LIKE ?) ");
            String like = "%" + filter.getSearchTerm().trim() + "%";
            params.add(like); params.add(like); params.add(like); params.add(like);
        }

        String sortCol = SORT_COLUMNS.getOrDefault(filter.getSortField(), "patient_name");
        String sortDir = "desc".equalsIgnoreCase(filter.getSortDir()) ? "DESC" : "ASC";

        long total = 0;
        String countSql = "SELECT COUNT(*) FROM patients " + where;
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(countSql)) {
            int idx = 1;
            for (Object p : params) ps.setObject(idx++, p);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) total = rs.getLong(1); }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting patients: " + e.getMessage(), e);
        }

        int page = Math.max(1, filter.getPage());
        int pageSize = Math.max(1, filter.getPageSize());
        int offset = (page - 1) * pageSize;

        List<Patient> items = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM patients " + where +
                     " ORDER BY " + sortCol + " " + sortDir + " LIMIT ? OFFSET ?";
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
            throw new RuntimeException("Error loading patients: " + e.getMessage(), e);
        }
        return new PageResult<>(items, page, pageSize, total);
    }

    @Override
    public void delete(int patientId) {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting patient: " + e.getMessage(), e);
        }
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setPatientName(rs.getString("patient_name"));
        p.setAddress(rs.getString("address"));
        p.setContactNumber(rs.getString("contact_number"));
        int birthYear = rs.getInt("birth_year");
        p.setBirthYear(rs.wasNull() ? null : birthYear);
        p.setGender(rs.getString("gender"));
        return p;
    }
}

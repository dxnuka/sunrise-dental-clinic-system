package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.ReportDAO;
import com.sunrise.dental.db.DBConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportDAOImpl implements ReportDAO {

    @Override
    public List<Map<String, Object>> revenueByTreatment(LocalDate from, LocalDate to) {
        String sql = "SELECT t.treatment_name, COUNT(b.bill_id) AS bills_issued, " +
                     "COALESCE(SUM(b.total_amount), 0) AS total_revenue " +
                     "FROM bills b " +
                     "JOIN appointments a ON b.appointment_id = a.appointment_id " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "WHERE DATE(b.generated_at) BETWEEN ? AND ? " +
                     "GROUP BY t.treatment_name ORDER BY total_revenue DESC";
        return runRangedQuery(sql, from, to);
    }

    @Override
    public List<Map<String, Object>> dentistWorkload(LocalDate from, LocalDate to) {
        String sql = "SELECT d.dentist_name, COUNT(a.appointment_id) AS total_appointments, " +
                     "SUM(CASE WHEN a.status='COMPLETED' THEN 1 ELSE 0 END) AS completed " +
                     "FROM dentists d " +
                     "LEFT JOIN appointments a ON d.dentist_id = a.dentist_id " +
                     "AND a.appointment_date BETWEEN ? AND ? " +
                     "GROUP BY d.dentist_name ORDER BY total_appointments DESC";
        return runRangedQuery(sql, from, to);
    }

    @Override
    public List<Map<String, Object>> outstandingBills(LocalDate from, LocalDate to) {
        String sql = "SELECT b.bill_id, a.appointment_number, p.patient_id, p.patient_name, b.total_amount, b.generated_at " +
                     "FROM bills b " +
                     "JOIN appointments a ON b.appointment_id = a.appointment_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "WHERE b.payment_status = 'UNPAID' AND DATE(b.generated_at) BETWEEN ? AND ? " +
                     "ORDER BY b.generated_at DESC";
        return runRangedQuery(sql, from, to);
    }

    private List<Map<String, Object>> runRangedQuery(String sql, LocalDate from, LocalDate to) {
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                return toList(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error running report: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> toList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= cols; i++) row.put(md.getColumnLabel(i), rs.getObject(i));
            rows.add(row);
        }
        return rows;
    }
}

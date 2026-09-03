package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.TreatmentDAO;
import com.sunrise.dental.db.DBConnectionManager;
import com.sunrise.dental.model.Treatment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAOImpl implements TreatmentDAO {

    private static final String COLUMNS = "treatment_id, treatment_name, base_fee, duration_minutes";

    @Override
    public List<Treatment> findAll() {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM treatments ORDER BY treatment_name";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error loading treatments: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Treatment findById(int treatmentId) {
        String sql = "SELECT " + COLUMNS + " FROM treatments WHERE treatment_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, treatmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading treatment: " + e.getMessage(), e);
        }
        return null;
    }

    private Treatment mapRow(ResultSet rs) throws SQLException {
        return new Treatment(rs.getInt("treatment_id"), rs.getString("treatment_name"),
                rs.getBigDecimal("base_fee"), rs.getInt("duration_minutes"));
    }
}

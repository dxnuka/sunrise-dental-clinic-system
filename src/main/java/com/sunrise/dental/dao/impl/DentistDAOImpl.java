package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.DentistDAO;
import com.sunrise.dental.db.DBConnectionManager;
import com.sunrise.dental.model.Dentist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAOImpl implements DentistDAO {

    private static final String COLUMNS = "dentist_id, dentist_name, specialization, consultation_fee";

    @Override
    public List<Dentist> findAllActive() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM dentists WHERE active = TRUE ORDER BY dentist_name";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error loading dentists: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Dentist findById(int dentistId) {
        String sql = "SELECT " + COLUMNS + " FROM dentists WHERE dentist_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dentistId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading dentist: " + e.getMessage(), e);
        }
        return null;
    }

    private Dentist mapRow(ResultSet rs) throws SQLException {
        return new Dentist(rs.getInt("dentist_id"), rs.getString("dentist_name"),
                rs.getString("specialization"), rs.getBigDecimal("consultation_fee"));
    }
}

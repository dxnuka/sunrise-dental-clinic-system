package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.DentistDAO;
import com.sunrise.dental.db.DBConnectionManager;
import com.sunrise.dental.model.Dentist;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAOImpl implements DentistDAO {

    private static final String COLUMNS = "dentist_id, dentist_name, specialization, consultation_fee, active";

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
    public List<Dentist> findAll() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM dentists ORDER BY active DESC, dentist_name";
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

    @Override
    public int create(String dentistName, String specialization, BigDecimal consultationFee) {
        String sql = "INSERT INTO dentists(dentist_name, specialization, consultation_fee, active) VALUES (?,?,?,TRUE)";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dentistName);
            ps.setString(2, specialization);
            ps.setBigDecimal(3, consultationFee);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating dentist: " + e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public void deactivate(int dentistId) {
        String sql = "UPDATE dentists SET active = FALSE WHERE dentist_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dentistId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating dentist: " + e.getMessage(), e);
        }
    }

    private Dentist mapRow(ResultSet rs) throws SQLException {
        Dentist d = new Dentist(rs.getInt("dentist_id"), rs.getString("dentist_name"),
                rs.getString("specialization"), rs.getBigDecimal("consultation_fee"));
        d.setActive(rs.getBoolean("active"));
        return d;
    }
}

package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.UserDAO;
import com.sunrise.dental.db.DBConnectionManager;
import com.sunrise.dental.model.User;

import java.sql.*;

public class UserDAOImpl implements UserDAO {

    private static final String COLUMNS =
            "user_id, username, password_hash, full_name, birth_year, gender, role";

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE username = ?";
        return queryOne(sql, username);
    }

    @Override
    public User findById(int userId) {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE user_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading user: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking username: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public int create(User user) {
        String sql = "INSERT INTO users(username, password_hash, full_name, birth_year, gender, role) " +
                     "VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            if (user.getBirthYear() != null) ps.setInt(4, user.getBirthYear()); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, user.getGender());
            ps.setString(6, user.getRole());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public void updateProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, birth_year = ?, gender = ? WHERE user_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            if (user.getBirthYear() != null) ps.setInt(2, user.getBirthYear()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, user.getGender());
            ps.setInt(4, user.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating profile: " + e.getMessage(), e);
        }
    }

    @Override
    public void updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating password: " + e.getMessage(), e);
        }
    }

    private User queryOne(String sql, String username) {
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error looking up user: " + e.getMessage(), e);
        }
        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        int by = rs.getInt("birth_year");
        u.setBirthYear(rs.wasNull() ? null : by);
        u.setGender(rs.getString("gender"));
        u.setRole(rs.getString("role"));
        return u;
    }
}

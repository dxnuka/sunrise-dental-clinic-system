package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.UserDAO;
import com.sunrise.dental.dao.UserFilter;
import com.sunrise.dental.db.DBConnectionManager;
import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAOImpl implements UserDAO {

    private static final String COLUMNS =
            "user_id, username, password_hash, full_name, birth_year, gender, role";

    private static final Map<String, String> SORT_COLUMNS = new HashMap<>();
    static {
        SORT_COLUMNS.put("name", "full_name");
        SORT_COLUMNS.put("username", "username");
        SORT_COLUMNS.put("role", "role");
    }

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

    @Override
    public PageResult<User> findPaged(UserFilter filter) {
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (filter.getSearchTerm() != null && !filter.getSearchTerm().trim().isEmpty()) {
            where.append(" AND (full_name LIKE ? OR username LIKE ?) ");
            String like = "%" + filter.getSearchTerm().trim() + "%";
            params.add(like); params.add(like);
        }
        if (filter.getRole() != null && !filter.getRole().trim().isEmpty()) {
            where.append(" AND role = ? ");
            params.add(filter.getRole());
        }

        String sortCol = SORT_COLUMNS.getOrDefault(filter.getSortField(), "full_name");
        String sortDir = "desc".equalsIgnoreCase(filter.getSortDir()) ? "DESC" : "ASC";

        long total = 0;
        String countSql = "SELECT COUNT(*) FROM users " + where;
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(countSql)) {
            int idx = 1;
            for (Object p : params) ps.setObject(idx++, p);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) total = rs.getLong(1); }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting users: " + e.getMessage(), e);
        }

        int page = Math.max(1, filter.getPage());
        int pageSize = Math.max(1, filter.getPageSize());
        int offset = (page - 1) * pageSize;

        List<User> items = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM users " + where +
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
            throw new RuntimeException("Error loading users: " + e.getMessage(), e);
        }
        return new PageResult<>(items, page, pageSize, total);
    }

    @Override
    public void delete(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
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

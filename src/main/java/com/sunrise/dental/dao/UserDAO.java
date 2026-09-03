package com.sunrise.dental.dao;

import com.sunrise.dental.model.User;

public interface UserDAO {
    User findByUsername(String username);
    User findById(int userId);
    boolean existsByUsername(String username);

    /** Inserts a new staff account (password_hash must already be hashed). Returns the new user_id. */
    int create(User user);

    /** Updates full_name/birth_year/gender/role for an existing user (not username or password). */
    void updateProfile(User user);

    /** Updates only the password hash for a given user - used by a future "change password" feature. */
    void updatePassword(int userId, String newPasswordHash);
}

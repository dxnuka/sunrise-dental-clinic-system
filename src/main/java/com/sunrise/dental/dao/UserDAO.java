package com.sunrise.dental.dao;

import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.User;

public interface UserDAO {
    User findByUsername(String username);
    User findById(int userId);
    boolean existsByUsername(String username);

    int create(User user);

    void updateProfile(User user);

    void updatePassword(int userId, String newPasswordHash);

    PageResult<User> findPaged(UserFilter filter);

    void delete(int userId);
}

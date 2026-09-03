package com.sunrise.dental.service;

import com.sunrise.dental.dao.UserDAO;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.factory.DAOFactory;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.PasswordUtil;
import com.sunrise.dental.util.ValidationUtil;

/** Business logic tier: authentication, self-registration, staff account
 *  creation, and profile edits. Controllers never talk to UserDAO directly. */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() { this(DAOFactory.getUserDAO()); }
    public AuthService(UserDAO userDAO) { this.userDAO = userDAO; }

    public User login(String username, String plainPassword) {
        User user = userDAO.findByUsername(username);
        if (user == null) return null;
        if (!PasswordUtil.matches(plainPassword, user.getPasswordHash())) return null;
        return user;
    }

    /** Used by both the public self-registration page and the admin "Add New User" form. */
    public int register(String username, String plainPassword, String fullName,
                         Integer birthYear, String gender, String role) throws ValidationException {

        ValidationUtil.requireValidUsername(username);
        ValidationUtil.requireValidPassword(plainPassword);
        ValidationUtil.requireValidName(fullName, "Full name");
        ValidationUtil.requireValidBirthYear(birthYear);
        ValidationUtil.requireValidGender(gender);

        if (userDAO.existsByUsername(username.trim())) {
            throw new ValidationException("That username is already taken. Please choose another.");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPasswordHash(PasswordUtil.hash(plainPassword));
        user.setFullName(fullName.trim());
        user.setBirthYear(birthYear);
        user.setGender(gender);
        user.setRole(("ADMIN".equals(role)) ? "ADMIN" : "RECEPTIONIST");

        return userDAO.create(user);
    }

    public void updateProfile(int userId, String fullName, Integer birthYear, String gender) throws ValidationException {
        ValidationUtil.requireValidName(fullName, "Full name");
        ValidationUtil.requireValidBirthYear(birthYear);
        ValidationUtil.requireValidGender(gender);

        User user = new User();
        user.setUserId(userId);
        user.setFullName(fullName.trim());
        user.setBirthYear(birthYear);
        user.setGender(gender);
        userDAO.updateProfile(user);
    }

    public User findById(int userId) {
        return userDAO.findById(userId);
    }
}

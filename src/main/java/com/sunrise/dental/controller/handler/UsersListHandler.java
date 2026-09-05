package com.sunrise.dental.controller.handler;

import com.sunrise.dental.dao.UserFilter;
import com.sunrise.dental.model.PageResult;
import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UsersListHandler implements RequestHandler {
    private final AuthService authService = new AuthService();

    @Override
    public String handle(HttpServletRequest request, HttpServletResponse response) {
        UserFilter filter = new UserFilter();
        filter.setSearchTerm(request.getParameter("q"));
        filter.setRole(emptyToNull(request.getParameter("role")));
        filter.setSortField(defaultIfBlank(request.getParameter("sort"), "name"));
        filter.setSortDir(defaultIfBlank(request.getParameter("dir"), "asc"));
        filter.setPage(parseIntOrDefault(request.getParameter("page"), 1));
        filter.setPageSize(9);

        PageResult<User> page = authService.findPaged(filter);
        request.setAttribute("usersPage", page);
        request.setAttribute("filter", filter);
        return "users.jsp";
    }

    private int parseIntOrDefault(String s, int def) {
        try { return (s == null || s.trim().isEmpty()) ? def : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }
    private String emptyToNull(String s) { return (s == null || s.trim().isEmpty()) ? null : s.trim(); }
    private String defaultIfBlank(String s, String def) { return (s == null || s.trim().isEmpty()) ? def : s.trim(); }
}

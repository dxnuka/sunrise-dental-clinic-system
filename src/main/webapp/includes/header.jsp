<%@ page import="com.sunrise.dental.model.User" %>
<%
    User loggedInUser = (User) session.getAttribute("loggedInUser");
    String currentAction = request.getParameter("action");
    if (currentAction == null) currentAction = "dashboard";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<header class="topbar">
    <div class="brand">
        <span class="brand-logo"><img src="<%= request.getContextPath() %>/images/logo.png" alt="Sunrise Dental Clinic logo"></span>
        <span class="brand-text">Sunrise Dental Clinic<span class="brand-sub">Appointment &amp; Patient Management</span></span>
    </div>
    <% if (loggedInUser != null) { %>
    <nav class="topnav">
        <a href="<%= request.getContextPath() %>/control?action=dashboard" class="<%= "dashboard".equals(currentAction) ? "active" : "" %>">Appointments</a>
        <a href="<%= request.getContextPath() %>/control?action=addAppointmentPage" class="<%= "addAppointmentPage".equals(currentAction) ? "active" : "" %>">Add Appointment</a>
        <a href="<%= request.getContextPath() %>/control?action=patients" class="<%= "patients".equals(currentAction) ? "active" : "" %>">Patients</a>
        <a href="<%= request.getContextPath() %>/control?action=reports" class="<%= "reports".equals(currentAction) ? "active" : "" %>">Reports</a>
        <% if ("ADMIN".equals(loggedInUser.getRole())) { %>
        <a href="<%= request.getContextPath() %>/control?action=users" class="<%= "users".equals(currentAction) ? "active" : "" %>">Manage Users</a>
        <a href="<%= request.getContextPath() %>/control?action=addUserPage" class="<%= "addUserPage".equals(currentAction) ? "active" : "" %>">Add User</a>
        <a href="<%= request.getContextPath() %>/control?action=dentistsPage" class="<%= "dentistsPage".equals(currentAction) ? "active" : "" %>">Dentists</a>
        <% } %>
        <a href="<%= request.getContextPath() %>/control?action=help" class="<%= "help".equals(currentAction) ? "active" : "" %>">Help</a>
        <a href="<%= request.getContextPath() %>/control?action=profile" class="user-chip-link">
            <span class="user-chip"><%= loggedInUser.getFullName() %></span>
        </a>
        <a href="<%= request.getContextPath() %>/control?action=logout" class="logout-link" title="Exit / Log out">&#9211;</a>
    </nav>
    <% } %>
</header>
<main class="container">

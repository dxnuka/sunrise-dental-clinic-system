<%@ page import="com.sunrise.dental.model.*, java.util.List" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<%
    User viewedUser = (User) request.getAttribute("viewedUser");
    List<Appointment> createdAppointments = (List<Appointment>) request.getAttribute("createdAppointments");
    String ctx = request.getContextPath();
%>

<% if (viewedUser == null) { %>
    <div class="card">
        <p>User not found. Go back to <a href="<%= ctx %>/control?action=users">Manage Users</a>.</p>
    </div>
<% } else { %>
<div class="card">
    <h2><%= viewedUser.getFullName() %>
        <span class="badge <%= "ADMIN".equals(viewedUser.getRole()) ? "badge-admin" : "badge-receptionist" %>"><%= viewedUser.getRole() %></span>
    </h2>
    <table>
        <tr><th>User ID</th><td><%= viewedUser.getUserId() %></td></tr>
        <tr><th>Username</th><td><%= viewedUser.getUsername() %></td></tr>
        <tr><th>Birth Year</th><td><%= viewedUser.getBirthYear() != null ? viewedUser.getBirthYear() : "-" %></td></tr>
        <tr><th>Gender</th><td><%= viewedUser.getGender() != null ? viewedUser.getGender() : "-" %></td></tr>
    </table>
    <a href="<%= ctx %>/control?action=users" class="btn btn-outline" style="margin-top:14px;">&larr; Back to Manage Users</a>
</div>

<div class="card">
    <h2>Appointments Registered by This User <small><%= createdAppointments != null ? createdAppointments.size() : 0 %> total</small></h2>
    <% if (createdAppointments == null || createdAppointments.isEmpty()) { %>
        <div class="empty-state">This user hasn't registered any appointments yet.</div>
    <% } else { %>
    <table>
        <tr><th>Appt #</th><th>Patient</th><th>Dentist</th><th>Date &amp; Time</th><th>Treatment</th><th>Status</th></tr>
        <% for (Appointment a : createdAppointments) {
            String badgeClass = "SCHEDULED".equals(a.getStatus()) ? "badge-scheduled"
                    : "COMPLETED".equals(a.getStatus()) ? "badge-completed" : "badge-cancelled";
        %>
        <tr>
            <td><a href="<%= ctx %>/control?action=viewAppointment&appointmentNumber=<%= a.getAppointmentNumber() %>"><%= a.getAppointmentNumber() %></a></td>
            <td><%= a.getPatient().getPatientName() %></td>
            <td><%= a.getDentist().getDentistName() %></td>
            <td><%= a.getAppointmentDate() %> at <%= a.getAppointmentTime() %></td>
            <td><%= a.getTreatment().getTreatmentName() %></td>
            <td><span class="badge <%= badgeClass %>"><%= a.getStatus() %></span></td>
        </tr>
        <% } %>
    </table>
    <% } %>
</div>
<% } %>

<%@ include file="includes/footer.jsp" %>

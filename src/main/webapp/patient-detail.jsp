<%@ page import="com.sunrise.dental.model.*" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<%
    PatientSummary summary = (PatientSummary) request.getAttribute("summary");
    String ctx = request.getContextPath();
%>

<% if (summary == null) { %>
    <div class="card">
        <p>Patient not found. Go back to the <a href="<%= ctx %>/control?action=patients">Patients list</a>.</p>
    </div>
<% } else {
    Patient patient = summary.getPatient();
    Appointment last = summary.getLastAppointment();
    Appointment next = summary.getNextAppointment();
%>
<div class="card">
    <h2><%= patient.getPatientName() %></h2>
    <table>
        <tr><th>Patient ID</th><td><%= patient.getPatientId() %></td></tr>
        <tr><th>Contact Number</th><td><%= patient.getContactNumber() %></td></tr>
        <tr><th>Address</th><td><%= patient.getAddress() %></td></tr>
        <tr><th>Birth Year</th><td><%= patient.getBirthYear() != null ? patient.getBirthYear() : "-" %></td></tr>
        <tr><th>Gender</th><td><%= patient.getGender() != null ? patient.getGender() : "-" %></td></tr>
    </table>
</div>

<div class="card">
    <h2>Appointment Summary</h2>
    <div class="stat-row">
        <div class="stat-card">
            <div class="stat-label">Total Appointments</div>
            <div class="stat-value"><%= summary.getTotalAppointments() %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Last Appointment</div>
            <div class="stat-value">
                <% if (last != null) { %><%= last.getAppointmentDate() %><% } else { %>-<% } %>
            </div>
            <div class="stat-sub">
                <% if (last != null) { %><%= last.getAppointmentTime() %> &middot; <%= last.getTreatment().getTreatmentName() %>
                <% } else { %>No past appointments<% } %>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Next Appointment</div>
            <div class="stat-value">
                <% if (next != null) { %><%= next.getAppointmentDate() %><% } else { %>-<% } %>
            </div>
            <div class="stat-sub">
                <% if (next != null) { %><%= next.getAppointmentTime() %> &middot; <%= next.getTreatment().getTreatmentName() %>
                <% } else { %>None scheduled<% } %>
            </div>
        </div>
    </div>
</div>
<% } %>

<%@ include file="includes/footer.jsp" %>

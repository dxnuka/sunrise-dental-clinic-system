<%@ page import="com.sunrise.dental.model.Appointment" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<div class="card">
    <h2>Appointment Details</h2>
    <%
        Appointment appointment = (Appointment) request.getAttribute("appointment");
        if (appointment != null) {
            String badgeClass = "SCHEDULED".equals(appointment.getStatus()) ? "badge-scheduled"
                    : "COMPLETED".equals(appointment.getStatus()) ? "badge-completed" : "badge-cancelled";
    %>
    <table>
        <tr><th>Appointment Number</th><td><%= appointment.getAppointmentNumber() %></td></tr>
        <tr><th>Status</th><td><span class="badge <%= badgeClass %>"><%= appointment.getStatus() %></span></td></tr>
        <tr><th>Patient ID</th><td><%= appointment.getPatient().getPatientId() %></td></tr>
        <tr><th>Patient Name</th><td><%= appointment.getPatient().getPatientName() %></td></tr>
        <tr><th>Address</th><td><%= appointment.getPatient().getAddress() %></td></tr>
        <tr><th>Contact Number</th><td><%= appointment.getPatient().getContactNumber() %></td></tr>
        <tr><th>Dentist</th><td><%= appointment.getDentist().getDentistName() %> (<%= appointment.getDentist().getSpecialization() %>)</td></tr>
        <tr><th>Treatment</th><td><%= appointment.getTreatment().getTreatmentName() %> (<%= appointment.getTreatment().getDurationMinutes() %> min)</td></tr>
        <tr><th>Date</th><td><%= appointment.getAppointmentDate() %></td></tr>
        <tr><th>Time</th><td><%= appointment.getAppointmentTime() %></td></tr>
        <tr><th>Created By (User ID)</th><td><%= appointment.getCreatedByUserId() != null ? appointment.getCreatedByUserId() : "null" %></td></tr>
    </table>
    <form method="post" action="<%= request.getContextPath() %>/control" style="margin-top:14px; display:inline-block;">
        <input type="hidden" name="action" value="generateBill">
        <input type="hidden" name="appointmentNumber" value="<%= appointment.getAppointmentNumber() %>">
        <button type="submit" class="btn">Generate &amp; Print Bill</button>
    </form>
    <a href="<%= request.getContextPath() %>/control?action=dashboard" class="btn btn-outline" style="margin-left:8px;">&larr; Back to Appointments</a>

    <% if ("SCHEDULED".equals(appointment.getStatus())) { %>
    <div class="status-actions">
        <form method="post" action="<%= request.getContextPath() %>/control" style="display:inline-block;">
            <input type="hidden" name="action" value="updateAppointmentStatus">
            <input type="hidden" name="appointmentNumber" value="<%= appointment.getAppointmentNumber() %>">
            <input type="hidden" name="newStatus" value="COMPLETED">
            <button type="submit" class="btn btn-secondary">Mark as Completed</button>
        </form>
        <form method="post" action="<%= request.getContextPath() %>/control" style="display:inline-block;"
              onsubmit="return confirm('Cancel this appointment? This cannot be undone.');">
            <input type="hidden" name="action" value="updateAppointmentStatus">
            <input type="hidden" name="appointmentNumber" value="<%= appointment.getAppointmentNumber() %>">
            <input type="hidden" name="newStatus" value="CANCELLED">
            <button type="submit" class="btn" style="background:#c0392b;">Cancel Appointment</button>
        </form>
    </div>
    <% } %>
    <% } else { %>
        <p>No appointment to display. Search from the <a href="<%= request.getContextPath() %>/control?action=dashboard">appointments list</a>.</p>
    <% } %>
</div>

<%@ include file="includes/footer.jsp" %>

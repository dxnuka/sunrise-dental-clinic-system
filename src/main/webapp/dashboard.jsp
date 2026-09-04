<%@ page import="com.sunrise.dental.model.*, com.sunrise.dental.dao.AppointmentFilter, java.util.List" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<%
    PageResult<Appointment> appointmentsPage = (PageResult<Appointment>) request.getAttribute("appointmentsPage");
    AppointmentFilter filter = (AppointmentFilter) request.getAttribute("filter");
    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
    List<Treatment> treatments = (List<Treatment>) request.getAttribute("treatments");
    String ctx = request.getContextPath();

    // Reusable query-string builder that keeps the current filters when changing page/sort.
    java.util.function.Function<Integer, String> pageLink = (pageNum) -> {
        StringBuilder sb = new StringBuilder(ctx + "/control?action=dashboard&page=" + pageNum);
        if (filter.getSearchTerm() != null) sb.append("&q=").append(java.net.URLEncoder.encode(filter.getSearchTerm(), java.nio.charset.StandardCharsets.UTF_8));
        if (filter.getDentistId() != null) sb.append("&dentistId=").append(filter.getDentistId());
        if (filter.getTreatmentId() != null) sb.append("&treatmentId=").append(filter.getTreatmentId());
        if (filter.getStatus() != null) sb.append("&status=").append(filter.getStatus());
        sb.append("&sort=").append(filter.getSortField()).append("&dir=").append(filter.getSortDir());
        return sb.toString();
    };
%>

<div class="card">
    <h2>All Appointments <small><%= appointmentsPage.getTotalItems() %> total</small></h2>

    <form method="get" action="<%= ctx %>/control" class="toolbar">
        <input type="hidden" name="action" value="dashboard">
        <div class="field">
            <label>Search</label>
            <input type="text" name="q" placeholder="Patient, appt #, dentist, treatment..."
                   value="<%= filter.getSearchTerm() != null ? filter.getSearchTerm() : "" %>">
        </div>
        <div class="field">
            <label>Dentist</label>
            <select name="dentistId">
                <option value="">All Dentists</option>
                <% for (Dentist d : dentists) { %>
                <option value="<%= d.getDentistId() %>" <%= (filter.getDentistId() != null && filter.getDentistId() == d.getDentistId()) ? "selected" : "" %>><%= d.getDentistName() %></option>
                <% } %>
            </select>
        </div>
        <div class="field">
            <label>Treatment</label>
            <select name="treatmentId">
                <option value="">All Treatments</option>
                <% for (Treatment t : treatments) { %>
                <option value="<%= t.getTreatmentId() %>" <%= (filter.getTreatmentId() != null && filter.getTreatmentId() == t.getTreatmentId()) ? "selected" : "" %>><%= t.getTreatmentName() %></option>
                <% } %>
            </select>
        </div>
        <div class="field">
            <label>Status</label>
            <select name="status">
                <option value="">Any Status</option>
                <option value="SCHEDULED" <%= "SCHEDULED".equals(filter.getStatus()) ? "selected" : "" %>>Scheduled</option>
                <option value="COMPLETED" <%= "COMPLETED".equals(filter.getStatus()) ? "selected" : "" %>>Completed</option>
                <option value="CANCELLED" <%= "CANCELLED".equals(filter.getStatus()) ? "selected" : "" %>>Cancelled</option>
            </select>
        </div>
        <div class="field">
            <label>Sort by</label>
            <select name="sort">
                <option value="date" <%= "date".equals(filter.getSortField()) ? "selected" : "" %>>Date &amp; Time</option>
                <option value="patient" <%= "patient".equals(filter.getSortField()) ? "selected" : "" %>>Patient Name</option>
                <option value="dentist" <%= "dentist".equals(filter.getSortField()) ? "selected" : "" %>>Dentist</option>
                <option value="treatment" <%= "treatment".equals(filter.getSortField()) ? "selected" : "" %>>Treatment</option>
            </select>
        </div>
        <div class="field">
            <label>Direction</label>
            <select name="dir">
                <option value="asc" <%= "asc".equals(filter.getSortDir()) ? "selected" : "" %>>Ascending</option>
                <option value="desc" <%= "desc".equals(filter.getSortDir()) ? "selected" : "" %>>Descending</option>
            </select>
        </div>
        <div class="field" style="flex:0;">
            <button type="submit" class="btn btn-secondary">Apply</button>
        </div>
        <div class="field" style="flex:0;">
            <a href="<%= ctx %>/control?action=addAppointmentPage" class="btn">+ Add Appointment</a>
        </div>
    </form>

    <% if (appointmentsPage.getItems().isEmpty()) { %>
        <div class="empty-state">No appointments match your filters.</div>
    <% } else { %>
    <div class="grid-cards">
        <% for (Appointment a : appointmentsPage.getItems()) {
            String badgeClass = "SCHEDULED".equals(a.getStatus()) ? "badge-scheduled"
                    : "COMPLETED".equals(a.getStatus()) ? "badge-completed" : "badge-cancelled";
        %>
        <a class="entity-card" href="<%= ctx %>/control?action=viewAppointment&appointmentNumber=<%= a.getAppointmentNumber() %>">
            <div class="ec-title"><%= a.getPatient().getPatientName() %> <span class="badge <%= badgeClass %>"><%= a.getStatus() %></span></div>
            <div class="ec-row"><b>Appt #</b> <%= a.getAppointmentNumber() %></div>
            <div class="ec-row"><b>Dentist</b> <%= a.getDentist().getDentistName() %></div>
            <div class="ec-row"><b>Date &amp; Time</b> <%= a.getAppointmentDate() %> at <%= a.getAppointmentTime() %></div>
            <div class="ec-row"><b>Treatment</b> <%= a.getTreatment().getTreatmentName() %></div>
        </a>
        <% } %>
    </div>

    <div class="pagination">
        <% if (appointmentsPage.hasPrevious()) { %>
            <a href="<%= pageLink.apply(appointmentsPage.getCurrentPage() - 1) %>">&laquo; Prev</a>
        <% } else { %><span class="disabled">&laquo; Prev</span><% } %>

        <% for (int p = 1; p <= appointmentsPage.getTotalPages(); p++) { %>
            <% if (p == appointmentsPage.getCurrentPage()) { %>
                <span class="current"><%= p %></span>
            <% } else { %>
                <a href="<%= pageLink.apply(p) %>"><%= p %></a>
            <% } %>
        <% } %>

        <% if (appointmentsPage.hasNext()) { %>
            <a href="<%= pageLink.apply(appointmentsPage.getCurrentPage() + 1) %>">Next &raquo;</a>
        <% } else { %><span class="disabled">Next &raquo;</span><% } %>
    </div>
    <% } %>
</div>

<%@ include file="includes/footer.jsp" %>

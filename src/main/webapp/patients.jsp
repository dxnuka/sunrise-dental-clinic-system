<%@ page import="com.sunrise.dental.model.*, com.sunrise.dental.dao.PatientFilter" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<%
    PageResult<Patient> patientsPage = (PageResult<Patient>) request.getAttribute("patientsPage");
    PatientFilter filter = (PatientFilter) request.getAttribute("filter");
    String ctx = request.getContextPath();

    java.util.function.Function<Integer, String> pageLink = (pageNum) -> {
        StringBuilder sb = new StringBuilder(ctx + "/control?action=patients&page=" + pageNum);
        if (filter.getSearchTerm() != null) sb.append("&q=").append(java.net.URLEncoder.encode(filter.getSearchTerm(), java.nio.charset.StandardCharsets.UTF_8));
        sb.append("&sort=").append(filter.getSortField()).append("&dir=").append(filter.getSortDir());
        return sb.toString();
    };
%>

<div class="card">
    <h2>Patients <small><%= patientsPage.getTotalItems() %> total</small></h2>

    <form method="get" action="<%= ctx %>/control" class="toolbar">
        <input type="hidden" name="action" value="patients">
        <div class="field">
            <label>Search</label>
            <input type="text" name="q" placeholder="Name, contact number, or address..."
                   value="<%= filter.getSearchTerm() != null ? filter.getSearchTerm() : "" %>">
        </div>
        <div class="field">
            <label>Sort by</label>
            <select name="sort">
                <option value="name" <%= "name".equals(filter.getSortField()) ? "selected" : "" %>>Name</option>
                <option value="registered" <%= "registered".equals(filter.getSortField()) ? "selected" : "" %>>Date Registered</option>
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
    </form>

    <% if (patientsPage.getItems().isEmpty()) { %>
        <div class="empty-state">No patients match your search.</div>
    <% } else { %>
    <div class="grid-cards">
        <% for (Patient p : patientsPage.getItems()) { %>
        <a class="entity-card" href="<%= ctx %>/control?action=patientDetail&patientId=<%= p.getPatientId() %>">
            <div class="ec-title"><%= p.getPatientName() %></div>
            <div class="ec-row" style="color:#a9b6c0; font-size:.78rem; margin-top:-2px;">Patient ID: <%= p.getPatientId() %></div>
            <div class="ec-row"><b>Contact</b> <%= p.getContactNumber() %></div>
            <div class="ec-row"><b>Address</b> <%= p.getAddress() %></div>
            <div class="ec-row">
                <b>Birth Year</b> <%= p.getBirthYear() != null ? p.getBirthYear() : "-" %>
                &nbsp;&middot;&nbsp; <b>Gender</b> <%= p.getGender() != null ? p.getGender() : "-" %>
            </div>
        </a>
        <% } %>
    </div>

    <div class="pagination">
        <% if (patientsPage.hasPrevious()) { %>
            <a href="<%= pageLink.apply(patientsPage.getCurrentPage() - 1) %>">&laquo; Prev</a>
        <% } else { %><span class="disabled">&laquo; Prev</span><% } %>
        <% for (int p = 1; p <= patientsPage.getTotalPages(); p++) { %>
            <% if (p == patientsPage.getCurrentPage()) { %><span class="current"><%= p %></span>
            <% } else { %><a href="<%= pageLink.apply(p) %>"><%= p %></a><% } %>
        <% } %>
        <% if (patientsPage.hasNext()) { %>
            <a href="<%= pageLink.apply(patientsPage.getCurrentPage() + 1) %>">Next &raquo;</a>
        <% } else { %><span class="disabled">Next &raquo;</span><% } %>
    </div>
    <% } %>
</div>

<%@ include file="includes/footer.jsp" %>

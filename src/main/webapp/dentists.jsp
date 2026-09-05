<%@ page import="com.sunrise.dental.model.Dentist, java.util.List" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<%
    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
    String ctx = request.getContextPath();
%>

<div class="grid-2">
    <div class="card">
        <h2>Add Dentist</h2>
        <form method="post" action="<%= ctx %>/control">
            <input type="hidden" name="action" value="addDentist">

            <label>Dentist Name</label>
            <input type="text" name="dentistName" placeholder="e.g. Dr. Chamal Rathnayake"
                   pattern="[\p{L} .\-]{2,100}" title="Letters, spaces, hyphens and periods only" required>

            <label>Specialization</label>
            <input type="text" name="specialization" placeholder="e.g. General Dentistry" required>

            <label>Consultation Fee (LKR)</label>
            <input type="number" name="consultationFee" step="0.01" min="0.01" required>

            <button type="submit" class="btn btn-block">Add Dentist</button>
        </form>
    </div>

    <div class="card">
        <h2>All Dentists <small><%= dentists != null ? dentists.size() : 0 %> total</small></h2>
        <% if (dentists == null || dentists.isEmpty()) { %>
            <div class="empty-state">No dentists on record yet.</div>
        <% } else { %>
        <div class="grid-cards">
            <% for (Dentist d : dentists) { %>
            <div class="entity-card" style="cursor:default;">
                <div class="ec-title">
                    <%= d.getDentistName() %>
                    <% if (!d.isActive()) { %><span class="badge badge-cancelled">Inactive</span><% } %>
                </div>
                <div class="ec-row"><b>Specialization</b> <%= d.getSpecialization() %></div>
                <div class="ec-row"><b>Consultation Fee</b> LKR <%= d.getConsultationFee() %></div>
                <% if (d.isActive()) { %>
                <form method="post" action="<%= ctx %>/control" style="margin-top:10px;"
                      onsubmit="return confirm('Remove this dentist? They will no longer be bookable for new appointments, but their existing appointments and bills are kept.');">
                    <input type="hidden" name="action" value="deactivateDentist">
                    <input type="hidden" name="dentistId" value="<%= d.getDentistId() %>">
                    <button type="submit" class="btn btn-sm" style="background:#c0392b;">Delete</button>
                </form>
                <% } %>
            </div>
            <% } %>
        </div>
        <% } %>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>

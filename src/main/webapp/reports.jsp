<%@ page import="java.util.*, java.time.LocalDate" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<%
    String ctx = request.getContextPath();
    LocalDate revenueFrom = (LocalDate) request.getAttribute("revenueFrom");
    LocalDate revenueTo = (LocalDate) request.getAttribute("revenueTo");
    LocalDate workloadFrom = (LocalDate) request.getAttribute("workloadFrom");
    LocalDate workloadTo = (LocalDate) request.getAttribute("workloadTo");
    LocalDate outstandingFrom = (LocalDate) request.getAttribute("outstandingFrom");
    LocalDate outstandingTo = (LocalDate) request.getAttribute("outstandingTo");
%>

<!-- ============ Revenue by Treatment ============ -->
<div class="card">
    <h2>Revenue by Treatment Type</h2>
    <form method="get" action="<%= ctx %>/control" class="report-controls">
        <input type="hidden" name="action" value="reports">
        <input type="hidden" name="workloadFrom" value="<%= workloadFrom %>">
        <input type="hidden" name="workloadTo" value="<%= workloadTo %>">
        <input type="hidden" name="outstandingFrom" value="<%= outstandingFrom %>">
        <input type="hidden" name="outstandingTo" value="<%= outstandingTo %>">
        <div class="field"><label>From</label><input type="date" name="revenueFrom" value="<%= revenueFrom %>"></div>
        <div class="field"><label>To</label><input type="date" name="revenueTo" value="<%= revenueTo %>"></div>
        <div class="field" style="flex:0;"><button type="submit" class="btn btn-sm btn-secondary">Apply Range</button></div>
        <div class="field" style="flex:0;">
            <a class="btn btn-sm" href="<%= ctx %>/control?action=generateReportPdf&reportType=revenue&from=<%= revenueFrom %>&to=<%= revenueTo %>">Generate PDF</a>
        </div>
    </form>
    <table>
        <tr><th>Treatment</th><th>Bills Issued</th><th>Total Revenue (LKR)</th></tr>
        <% List<Map<String,Object>> revenue = (List<Map<String,Object>>) request.getAttribute("revenueByTreatment");
           if (revenue == null || revenue.isEmpty()) { %>
        <tr><td colspan="3" class="empty-state">No revenue recorded in this date range.</td></tr>
        <% } else for (Map<String,Object> r : revenue) { %>
        <tr><td><%= r.get("treatment_name") %></td><td><%= r.get("bills_issued") %></td><td><%= r.get("total_revenue") %></td></tr>
        <% } %>
    </table>
</div>

<!-- ============ Dentist Workload ============ -->
<div class="card">
    <h2>Dentist Workload</h2>
    <form method="get" action="<%= ctx %>/control" class="report-controls">
        <input type="hidden" name="action" value="reports">
        <input type="hidden" name="revenueFrom" value="<%= revenueFrom %>">
        <input type="hidden" name="revenueTo" value="<%= revenueTo %>">
        <input type="hidden" name="outstandingFrom" value="<%= outstandingFrom %>">
        <input type="hidden" name="outstandingTo" value="<%= outstandingTo %>">
        <div class="field"><label>From</label><input type="date" name="workloadFrom" value="<%= workloadFrom %>"></div>
        <div class="field"><label>To</label><input type="date" name="workloadTo" value="<%= workloadTo %>"></div>
        <div class="field" style="flex:0;"><button type="submit" class="btn btn-sm btn-secondary">Apply Range</button></div>
        <div class="field" style="flex:0;">
            <a class="btn btn-sm" href="<%= ctx %>/control?action=generateReportPdf&reportType=workload&from=<%= workloadFrom %>&to=<%= workloadTo %>">Generate PDF</a>
        </div>
    </form>
    <table>
        <tr><th>Dentist</th><th>Total Appointments</th><th>Completed</th></tr>
        <% List<Map<String,Object>> workload = (List<Map<String,Object>>) request.getAttribute("dentistWorkload");
           if (workload == null || workload.isEmpty()) { %>
        <tr><td colspan="3" class="empty-state">No appointments recorded in this date range.</td></tr>
        <% } else for (Map<String,Object> w : workload) { %>
        <tr><td><%= w.get("dentist_name") %></td><td><%= w.get("total_appointments") %></td><td><%= w.get("completed") %></td></tr>
        <% } %>
    </table>
</div>

<!-- ============ Outstanding Bills ============ -->
<div class="card">
    <h2>Outstanding (Unpaid) Bills</h2>
    <form method="get" action="<%= ctx %>/control" class="report-controls">
        <input type="hidden" name="action" value="reports">
        <input type="hidden" name="revenueFrom" value="<%= revenueFrom %>">
        <input type="hidden" name="revenueTo" value="<%= revenueTo %>">
        <input type="hidden" name="workloadFrom" value="<%= workloadFrom %>">
        <input type="hidden" name="workloadTo" value="<%= workloadTo %>">
        <div class="field"><label>From</label><input type="date" name="outstandingFrom" value="<%= outstandingFrom %>"></div>
        <div class="field"><label>To</label><input type="date" name="outstandingTo" value="<%= outstandingTo %>"></div>
        <div class="field" style="flex:0;"><button type="submit" class="btn btn-sm btn-secondary">Apply Range</button></div>
        <div class="field" style="flex:0;">
            <a class="btn btn-sm" href="<%= ctx %>/control?action=generateReportPdf&reportType=outstanding&from=<%= outstandingFrom %>&to=<%= outstandingTo %>">Generate PDF</a>
        </div>
    </form>
    <table>
        <tr><th>Bill No</th><th>Appointment No</th><th>Patient ID</th><th>Patient</th><th>Amount (LKR)</th></tr>
        <% List<Map<String,Object>> outstanding = (List<Map<String,Object>>) request.getAttribute("outstandingBills");
           if (outstanding == null || outstanding.isEmpty()) { %>
        <tr><td colspan="5" class="empty-state">No outstanding bills in this date range.</td></tr>
        <% } else for (Map<String,Object> o : outstanding) { %>
        <tr><td><%= o.get("bill_id") %></td><td><%= o.get("appointment_number") %></td><td><%= o.get("patient_id") %></td><td><%= o.get("patient_name") %></td><td><%= o.get("total_amount") %></td></tr>
        <% } %>
    </table>
</div>

<%@ include file="includes/footer.jsp" %>

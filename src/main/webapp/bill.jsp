<%@ page import="com.sunrise.dental.model.Appointment, com.sunrise.dental.model.Bill, java.time.format.DateTimeFormatter" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<div class="card">
    <h2>Patient Bill / Receipt</h2>
    <%
        Appointment appointment = (Appointment) request.getAttribute("appointment");
        Bill bill = (Bill) request.getAttribute("bill");
        if (appointment != null && bill != null) {
            DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a");
    %>
    <div class="bill-box">
        <p><b>Sunrise Dental Clinic</b> &mdash; Colombo</p>
        <p>Appointment No: <b><%= appointment.getAppointmentNumber() %></b> &nbsp;|&nbsp; Bill No: <b><%= bill.getBillId() %></b></p>
        <p>Patient: <%= appointment.getPatient().getPatientName() %></p>
        <p>Dentist: <%= appointment.getDentist().getDentistName() %></p>
        <p>Treatment: <%= appointment.getTreatment().getTreatmentName() %></p>
        <p>Appointment Date &amp; Time: <%= appointment.getAppointmentDate() %> at <%= appointment.getAppointmentTime() %></p>
        <p>Bill Generated: <%= bill.getGeneratedAt() != null ? bill.getGeneratedAt().format(dtFmt) : "-" %></p>
        <table>
            <tr><td>Base Treatment Fee</td><td>LKR <%= bill.getBaseFee() %></td></tr>
            <tr><td>Consultation Fee</td><td>LKR <%= bill.getConsultationFee() %></td></tr>
            <tr><td class="bill-total">Total Payable</td><td class="bill-total">LKR <%= bill.getTotalAmount() %></td></tr>
        </table>
        <p style="margin-top:10px;" class="no-print">
            <button onclick="window.print()" class="btn" type="button">Print This Bill</button>
        </p>
    </div>
    <% } else { %>
        <p>No bill to display. Generate one from the <a href="<%= request.getContextPath() %>/control?action=dashboard">dashboard</a>.</p>
    <% } %>
</div>

<%@ include file="includes/footer.jsp" %>

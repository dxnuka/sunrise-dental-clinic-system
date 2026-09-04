<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<div class="card">
    <h2>Help &amp; Quick-Start Guide for Staff</h2>

    <div class="help-step"><b>1. Log in or register.</b> Existing staff log in from the main page.
        New staff can click "Create an account" to self-register as a Receptionist; an existing
        admin can also create accounts of either role from the "Add User" page.</div>

    <div class="help-step"><b>2. Browse appointments.</b> The "Appointments" page lists every
        appointment as cards showing the patient, dentist, date/time, and treatment. Use the
        search box, the Dentist/Treatment/Status filters, and the sort dropdown above the list, and
        page through results at the bottom. Click any card to see its full details.</div>

    <div class="help-step"><b>3. Add a new appointment.</b> Go to "Add Appointment". Choose
        <b>New Patient</b> or <b>Existing Patient</b> with the toggle at the top:
        <ul>
            <li><b>New Patient</b> - enter their name, address, and contact number.</li>
            <li><b>Existing Patient</b> - type part of their name or contact number into the search
                box and click them from the suggestions list.</li>
        </ul>
        Then choose the dentist, treatment type, date, and time. Dates cannot be in the past, and
        times are restricted to fixed 30-minute blocks between 08:00 and 17:00. Every treatment has
        its own duration, and the system will not let you book a dentist for an overlapping slot -
        you'll see a clear warning if you try.</div>

    <div class="help-step"><b>4. Look up an appointment and bill.</b> Click any appointment card to
        open its details, then click "Generate &amp; Print Bill" to calculate the total (treatment
        fee + consultation fee, minus any loyalty discount) and show a printable receipt.</div>

    <div class="help-step"><b>5. Manage patients.</b> The "Patients" page lists every registered
        patient with search and pagination. Click a patient to see their details plus three summary
        cards: Total Appointments, Last Appointment, and Next Appointment (shown as "-" when there
        isn't one yet).</div>

    <div class="help-step"><b>6. View reports.</b> Each report table (Revenue by Treatment, Dentist
        Workload, Outstanding Bills) has its own date-range filter, defaulting to the last 30 days.
        Adjust the range and click "Apply Range" to refresh a table, or "Generate PDF" to download
        that table as a printable PDF with the clinic name, report title, and date range on it.</div>

    <div class="help-step"><b>7. Manage users (admins only).</b> Admins see two extra menu items:
        "Add User" creates a new login for a staff member as either Receptionist or Admin, and
        "Manage Users" lists every account with search, a role filter, and pagination - admins can
        delete a Receptionist account from there (Admin accounts can't be deleted through the UI).
        Receptionists don't see these pages, and are redirected away if they try to open the links
        directly.</div>

    <div class="help-step"><b>8. Update your profile.</b> Click your name in the top-right menu to
        view and edit your own full name, birth year, and gender. Your username and role cannot be
        changed from this page.</div>

    <div class="help-step"><b>9. Input rules.</b> Every form validates its inputs: names must be
        letters only (no numbers or symbols), contact numbers must be exactly 10 digits, birth
        years cannot be in the future, and appointment times must be valid clinic slots. If
        something is rejected, the message at the top of the page explains exactly what to fix.</div>

    <div class="help-step"><b>10. Exit the system.</b> Click the exit icon in the top-right at any
        time to safely end your session.</div>

    <div class="help-step"><b>Having trouble?</b> Contact the clinic administrator - do not share
        your password with anyone.</div>
</div>

<%@ include file="includes/footer.jsp" %>

<%@ page import="com.sunrise.dental.model.Dentist, com.sunrise.dental.model.Treatment, java.util.List" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<%
    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
    List<Treatment> treatments = (List<Treatment>) request.getAttribute("treatments");
    String ctx = request.getContextPath();
    String todayIso = java.time.LocalDate.now().toString();
%>

<div class="card card-narrow">
    <h2>Add New Appointment</h2>

    <div class="toggle-group">
        <input type="radio" id="modeNew" name="patientModeToggle" checked>
        <label for="modeNew" onclick="setPatientMode('new')">New Patient</label>
        <input type="radio" id="modeExisting" name="patientModeToggle">
        <label for="modeExisting" onclick="setPatientMode('existing')">Existing Patient</label>
    </div>

    <form method="post" action="<%= ctx %>/control" id="appointmentForm">
        <input type="hidden" name="action" value="registerAppointment">
        <input type="hidden" name="patientMode" id="patientModeField" value="new">
        <input type="hidden" name="existingPatientId" id="existingPatientId" value="">

        <div id="newPatientFields">
            <label>Patient Name</label>
            <input type="text" name="patientName" id="patientNameInput" pattern="[\p{L} \-]{2,100}"
                   title="Letters, spaces and hyphens only" required>
            <label>Address</label>
            <input type="text" name="address" id="addressInput" minlength="5" maxlength="255" required>
            <label>Contact Number</label>
            <input type="text" name="contactNumber" id="contactInput" pattern="[0-9]{10}"
                   maxlength="10" placeholder="10 digits" title="10 digits" required>

            <div class="grid-2">
                <div>
                    <label>Birth Year</label>
                    <input type="number" name="patientBirthYear" id="patientBirthYearInput"
                           min="1900" max="<%= java.time.Year.now().getValue() %>" required>
                </div>
                <div>
                    <label>Gender</label>
                    <select name="patientGender" id="patientGenderInput" required>
                        <option value="">Select...</option>
                        <option value="MALE">Male</option>
                        <option value="FEMALE">Female</option>
                        <option value="OTHER">Other</option>
                    </select>
                </div>
            </div>
        </div>

        <div id="existingPatientFields" style="display:none;">
            <label>Search Existing Patient</label>
            <div class="search-wrap">
                <input type="text" id="patientSearchBox" placeholder="Type a name, patient ID, or contact number...">
                <div class="search-suggest" id="patientSuggestions"></div>
            </div>
            <div class="selected-patient-chip" id="selectedPatientChip">
                <span id="selectedPatientLabel"></span>
                <button type="button" onclick="clearSelectedPatient()">&times; Change</button>
            </div>
        </div>

        <label>Dentist</label>
        <select name="dentistId" id="dentistSelect" required>
            <option value="">Select a dentist...</option>
            <% for (Dentist d : dentists) { %>
            <option value="<%= d.getDentistId() %>"><%= d.getDentistName() %> - <%= d.getSpecialization() %></option>
            <% } %>
        </select>

        <label>Treatment Type</label>
        <select name="treatmentId" id="treatmentSelect" required>
            <option value="">Select a treatment...</option>
            <% for (Treatment t : treatments) { %>
            <option value="<%= t.getTreatmentId() %>" data-duration="<%= t.getDurationMinutes() %>">
                <%= t.getTreatmentName() %> - LKR <%= t.getBaseFee() %> (<%= t.getDurationMinutes() %> min)
            </option>
            <% } %>
        </select>

        <label>Appointment Date</label>
        <input type="date" name="appointmentDate" id="appointmentDate" min="<%= todayIso %>" required>

        <label>Appointment Time</label>
        <select name="appointmentTime" id="appointmentTime" required disabled>
            <option value="">Select dentist, treatment &amp; date first...</option>
        </select>

        <button type="submit" class="btn btn-block">Register Appointment</button>
    </form>
</div>

<script>
function setPatientMode(mode) {
    document.getElementById('patientModeField').value = mode;
    var newFields = document.getElementById('newPatientFields');
    var existingFields = document.getElementById('existingPatientFields');
    var nameInput = document.getElementById('patientNameInput');
    var addressInput = document.getElementById('addressInput');
    var contactInput = document.getElementById('contactInput');
    var birthYearInput = document.getElementById('patientBirthYearInput');
    var genderInput = document.getElementById('patientGenderInput');

    var isExisting = (mode === 'existing');
    newFields.style.display = isExisting ? 'none' : 'block';
    existingFields.style.display = isExisting ? 'block' : 'none';
    [nameInput, addressInput, contactInput, birthYearInput, genderInput].forEach(function(el) {
        el.required = !isExisting;
    });
    if (!isExisting) clearSelectedPatient();
}

function clearSelectedPatient() {
    document.getElementById('existingPatientId').value = '';
    document.getElementById('selectedPatientChip').style.display = 'none';
    document.getElementById('patientSearchBox').value = '';
    document.getElementById('patientSearchBox').style.display = 'block';
}

(function() {
    var searchBox = document.getElementById('patientSearchBox');
    var suggestBox = document.getElementById('patientSuggestions');
    var debounceTimer = null;

    searchBox.addEventListener('input', function() {
        var q = searchBox.value.trim();
        clearTimeout(debounceTimer);
        if (q.length < 1) { suggestBox.style.display = 'none'; return; }
        debounceTimer = setTimeout(function() {
            fetch('<%= ctx %>/control?action=searchPatients&q=' + encodeURIComponent(q))
                .then(function(r) { return r.json(); })
                .then(function(list) {
                    suggestBox.innerHTML = '';
                    if (list.length === 0) {
                        suggestBox.innerHTML = '<div class="suggest-empty">No matching patients found.</div>';
                    } else {
                        list.forEach(function(p) {
                            var item = document.createElement('div');
                            item.className = 'suggest-item';
                            item.innerHTML = '<b>' + p.name + '</b> (ID: ' + p.id + ') &mdash; ' + p.contact;
                            item.onclick = function() {
                                document.getElementById('existingPatientId').value = p.id;
                                document.getElementById('selectedPatientLabel').textContent = p.name + ' (ID: ' + p.id + ', ' + p.contact + ')';
                                document.getElementById('selectedPatientChip').style.display = 'flex';
                                searchBox.value = '';
                                searchBox.style.display = 'none';
                                suggestBox.style.display = 'none';
                            };
                            suggestBox.appendChild(item);
                        });
                    }
                    suggestBox.style.display = 'block';
                });
        }, 250);
    });

    document.addEventListener('click', function(e) {
        if (!suggestBox.contains(e.target) && e.target !== searchBox) suggestBox.style.display = 'none';
    });
})();

// ---------------------------------------------------------------------------
// Dynamic time-slot picker: requires dentist + treatment + date all chosen,
// then fetches real availability from the server (which checks every
// existing appointment for that dentist on that date, accounting for each
// treatment's own duration) and renders every 15-minute slot with already-
// booked or already-passed slots disabled (greyed out, unselectable) rather
// than letting the user pick a conflicting slot and find out on submit.
// ---------------------------------------------------------------------------
(function() {
    var dentistSelect = document.getElementById('dentistSelect');
    var treatmentSelect = document.getElementById('treatmentSelect');
    var dateInput = document.getElementById('appointmentDate');
    var timeSelect = document.getElementById('appointmentTime');

    function resetTimeSelect(message) {
        timeSelect.innerHTML = '<option value="">' + message + '</option>';
        timeSelect.disabled = true;
    }

    function refreshAvailableSlots() {
        var dentistId = dentistSelect.value;
        var treatmentId = treatmentSelect.value;
        var date = dateInput.value;

        if (!dentistId || !treatmentId || !date) {
            resetTimeSelect('Select dentist, treatment &amp; date first...');
            return;
        }

        resetTimeSelect('Loading available times...');
        fetch('<%= ctx %>/control?action=availableSlots&dentistId=' + encodeURIComponent(dentistId) +
              '&treatmentId=' + encodeURIComponent(treatmentId) + '&date=' + encodeURIComponent(date))
            .then(function(r) { return r.json(); })
            .then(function(slots) {
                timeSelect.innerHTML = '';
                var placeholder = document.createElement('option');
                placeholder.value = '';
                placeholder.textContent = 'Select a time...';
                timeSelect.appendChild(placeholder);

                slots.forEach(function(slot) {
                    var opt = document.createElement('option');
                    opt.value = slot.time;
                    opt.textContent = slot.time + (slot.available ? '' : ' - Unavailable');
                    opt.disabled = !slot.available;
                    timeSelect.appendChild(opt);
                });
                timeSelect.disabled = false;
            })
            .catch(function() { resetTimeSelect('Could not load times - try again'); });
    }

    dentistSelect.addEventListener('change', refreshAvailableSlots);
    treatmentSelect.addEventListener('change', refreshAvailableSlots);
    dateInput.addEventListener('change', refreshAvailableSlots);
})();

// Guard against submitting "Existing Patient" mode without an actual selection
// (the "required" attribute has no effect on hidden inputs across browsers).
document.getElementById('appointmentForm').addEventListener('submit', function(e) {
    var mode = document.getElementById('patientModeField').value;
    var existingId = document.getElementById('existingPatientId').value;
    if (mode === 'existing' && !existingId) {
        e.preventDefault();
        alert('Please search for and select an existing patient before submitting.');
    }
});
</script>

<%@ include file="includes/footer.jsp" %>

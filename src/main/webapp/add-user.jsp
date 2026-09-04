<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<div class="card card-narrow">
    <h2>Add User</h2>
    <p class="field-hint">Create a login account for a new staff member. Only admins can see this page.</p>

    <form method="post" action="<%= request.getContextPath() %>/control" autocomplete="off">
        <input type="hidden" name="action" value="addUser">

        <label>Full Name</label>
        <input type="text" name="newFullName" pattern="[\p{L} \-]{2,100}"
               title="Letters, spaces and hyphens only" required autofocus>

        <label>Username</label>
        <input type="text" name="newUsername" pattern="[A-Za-z0-9_.]{4,30}"
               title="4-30 characters: letters, numbers, dots or underscores" required>

        <label>Password</label>
        <div class="password-wrap">
            <input type="password" name="newPassword" id="addUserPassword" minlength="8" title="At least 8 characters" required>
            <button type="button" class="password-toggle" data-target="addUserPassword" aria-label="Show password"></button>
        </div>

        <label>Confirm Password</label>
        <div class="password-wrap">
            <input type="password" name="newConfirmPassword" id="addUserConfirmPassword" minlength="8" required>
            <button type="button" class="password-toggle" data-target="addUserConfirmPassword" aria-label="Show password"></button>
        </div>

        <div class="grid-2">
            <div>
                <label>Birth Year</label>
                <input type="number" name="newBirthYear" min="1900" max="<%= java.time.Year.now().getValue() %>" required>
            </div>
            <div>
                <label>Gender</label>
                <select name="newGender" required>
                    <option value="">Select...</option>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="OTHER">Other</option>
                </select>
            </div>
        </div>

        <label>Role</label>
        <select name="newRole" required>
            <option value="RECEPTIONIST">Receptionist</option>
            <option value="ADMIN">Admin</option>
        </select>

        <button type="submit" class="btn btn-block">Create User</button>
    </form>
</div>

<%@ include file="includes/footer.jsp" %>

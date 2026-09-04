<%@ page import="com.sunrise.dental.model.User" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<%
    User profileUser = (User) request.getAttribute("profileUser");
    String ctx = request.getContextPath();
%>

<div class="card card-narrow">
    <h2>My Profile</h2>
    <p class="field-hint">Username: <b><%= profileUser.getUsername() %></b> &middot; Role: <b><%= profileUser.getRole() %></b>
       (username and role cannot be changed here)</p>

    <form method="post" action="<%= ctx %>/control">
        <input type="hidden" name="action" value="updateProfile">

        <label>Full Name</label>
        <input type="text" name="fullName" value="<%= profileUser.getFullName() %>"
               pattern="[\p{L} \-]{2,100}" title="Letters, spaces and hyphens only" required>

        <div class="grid-2">
            <div>
                <label>Birth Year</label>
                <input type="number" name="birthYear" min="1900" max="2026"
                       value="<%= profileUser.getBirthYear() != null ? profileUser.getBirthYear() : "" %>" required>
            </div>
            <div>
                <label>Gender</label>
                <select name="gender" required>
                    <option value="MALE" <%= "MALE".equals(profileUser.getGender()) ? "selected" : "" %>>Male</option>
                    <option value="FEMALE" <%= "FEMALE".equals(profileUser.getGender()) ? "selected" : "" %>>Female</option>
                    <option value="OTHER" <%= "OTHER".equals(profileUser.getGender()) ? "selected" : "" %>>Other</option>
                </select>
            </div>
        </div>

        <button type="submit" class="btn btn-block">Save Changes</button>
    </form>
</div>

<%@ include file="includes/footer.jsp" %>

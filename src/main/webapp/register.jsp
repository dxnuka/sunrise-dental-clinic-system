<%
    if (session.getAttribute("loggedInUser") != null) {
        response.sendRedirect(request.getContextPath() + "/control?action=dashboard");
        return;
    }
    String prevFullName = request.getAttribute("fullName") != null ? (String) request.getAttribute("fullName") : "";
    String prevUsername = request.getAttribute("username") != null ? (String) request.getAttribute("username") : "";
    Object prevBirthYear = request.getAttribute("birthYear");
    String prevGender = request.getAttribute("gender") != null ? (String) request.getAttribute("gender") : "";
%>
<%@ include file="includes/header.jsp" %>

<div class="login-wrap">
    <div class="card">
        <h2>Create a Staff Account</h2>
        <%@ include file="includes/messages.jsp" %>
        <form method="post" action="<%= request.getContextPath() %>/control" autocomplete="off">
            <input type="hidden" name="action" value="register">

            <label>Full Name</label>
            <input type="text" name="fullName" value="<%= prevFullName %>" pattern="[\p{L} \-]{2,100}"
                   title="Letters, spaces and hyphens only" required autofocus>

            <label>Username</label>
            <input type="text" name="username" value="<%= prevUsername %>" pattern="[A-Za-z0-9_.]{4,30}"
                   title="4-30 characters: letters, numbers, dots or underscores" required>

            <label>Password</label>
            <div class="password-wrap">
                <input type="password" name="password" id="regPassword" minlength="8" title="At least 8 characters" required>
                <button type="button" class="password-toggle" data-target="regPassword" aria-label="Show password"></button>
            </div>

            <label>Confirm Password</label>
            <div class="password-wrap">
                <input type="password" name="confirmPassword" id="regConfirmPassword" minlength="8" required>
                <button type="button" class="password-toggle" data-target="regConfirmPassword" aria-label="Show password"></button>
            </div>

            <div class="grid-2">
                <div>
                    <label>Birth Year</label>
                    <input type="number" name="birthYear" min="1900" max="2026"
                           value="<%= prevBirthYear != null ? prevBirthYear : "" %>" required>
                </div>
                <div>
                    <label>Gender</label>
                    <select name="gender" required>
                        <option value="">Select...</option>
                        <option value="MALE" <%= "MALE".equals(prevGender) ? "selected" : "" %>>Male</option>
                        <option value="FEMALE" <%= "FEMALE".equals(prevGender) ? "selected" : "" %>>Female</option>
                        <option value="OTHER" <%= "OTHER".equals(prevGender) ? "selected" : "" %>>Other</option>
                    </select>
                </div>
            </div>

            <button type="submit" class="btn btn-block">Create Account</button>
        </form>
        <div class="auth-switch">
            Already have an account? <a href="<%= request.getContextPath() %>/index.jsp">Back to login</a>
        </div>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>

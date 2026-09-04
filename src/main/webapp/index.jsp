<%@ page import="com.sunrise.dental.model.User" %>
<%
    if (session.getAttribute("loggedInUser") != null) {
        response.sendRedirect(request.getContextPath() + "/control?action=dashboard");
        return;
    }
%>
<%@ include file="includes/header.jsp" %>

<div class="login-wrap">
    <div class="card">
        <h2>Staff Login</h2>
        <%@ include file="includes/messages.jsp" %>
        <form method="post" action="<%= request.getContextPath() %>/control">
            <input type="hidden" name="action" value="login">
            <label>Username</label>
            <input type="text" name="username" required autofocus>
            <label>Password</label>
            <div class="password-wrap">
                <input type="password" name="password" id="loginPassword" required>
                <button type="button" class="password-toggle" data-target="loginPassword" aria-label="Show password"></button>
            </div>
            <button type="submit" class="btn btn-block">Log In</button>
        </form>
        <div class="auth-switch">
            New staff member? <a href="<%= request.getContextPath() %>/register.jsp">Create an account</a>
        </div>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>

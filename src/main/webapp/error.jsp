<%@ page isErrorPage="true" %>
<%@ include file="includes/header.jsp" %>
<div class="card">
    <h2>Something Went Wrong</h2>
    <%@ include file="includes/messages.jsp" %>
    <div class="alert alert-error">
        <%= request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") : "An unexpected error occurred. Please try again." %>
    </div>
    <a class="btn" href="<%= request.getContextPath() %>/control?action=dashboard">Back to Dashboard</a>
</div>
<%@ include file="includes/footer.jsp" %>

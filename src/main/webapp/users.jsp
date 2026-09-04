<%@ page import="com.sunrise.dental.model.*, com.sunrise.dental.dao.UserFilter" %>
<%@ include file="includes/header.jsp" %>
<%@ include file="includes/messages.jsp" %>

<%
    PageResult<User> usersPage = (PageResult<User>) request.getAttribute("usersPage");
    UserFilter filter = (UserFilter) request.getAttribute("filter");
    String ctx = request.getContextPath();

    java.util.function.Function<Integer, String> pageLink = (pageNum) -> {
        StringBuilder sb = new StringBuilder(ctx + "/control?action=users&page=" + pageNum);
        if (filter.getSearchTerm() != null) sb.append("&q=").append(java.net.URLEncoder.encode(filter.getSearchTerm(), java.nio.charset.StandardCharsets.UTF_8));
        if (filter.getRole() != null) sb.append("&role=").append(filter.getRole());
        sb.append("&sort=").append(filter.getSortField()).append("&dir=").append(filter.getSortDir());
        return sb.toString();
    };
%>

<div class="card">
    <h2>Manage Users <small><%= usersPage.getTotalItems() %> total</small></h2>

    <form method="get" action="<%= ctx %>/control" class="toolbar">
        <input type="hidden" name="action" value="users">
        <div class="field">
            <label>Search</label>
            <input type="text" name="q" placeholder="Name or username..."
                   value="<%= filter.getSearchTerm() != null ? filter.getSearchTerm() : "" %>">
        </div>
        <div class="field">
            <label>Role</label>
            <select name="role">
                <option value="">All Roles</option>
                <option value="RECEPTIONIST" <%= "RECEPTIONIST".equals(filter.getRole()) ? "selected" : "" %>>Receptionist</option>
                <option value="ADMIN" <%= "ADMIN".equals(filter.getRole()) ? "selected" : "" %>>Admin</option>
            </select>
        </div>
        <div class="field">
            <label>Sort by</label>
            <select name="sort">
                <option value="name" <%= "name".equals(filter.getSortField()) ? "selected" : "" %>>Full Name</option>
                <option value="username" <%= "username".equals(filter.getSortField()) ? "selected" : "" %>>Username</option>
                <option value="role" <%= "role".equals(filter.getSortField()) ? "selected" : "" %>>Role</option>
            </select>
        </div>
        <div class="field">
            <label>Direction</label>
            <select name="dir">
                <option value="asc" <%= "asc".equals(filter.getSortDir()) ? "selected" : "" %>>Ascending</option>
                <option value="desc" <%= "desc".equals(filter.getSortDir()) ? "selected" : "" %>>Descending</option>
            </select>
        </div>
        <div class="field" style="flex:0;">
            <button type="submit" class="btn btn-secondary">Apply</button>
        </div>
        <div class="field" style="flex:0;">
            <a href="<%= ctx %>/control?action=addUserPage" class="btn">+ Add User</a>
        </div>
    </form>

    <% if (usersPage.getItems().isEmpty()) { %>
        <div class="empty-state">No users match your search.</div>
    <% } else { %>
    <div class="grid-cards">
        <% for (User u : usersPage.getItems()) {
            boolean isAdmin = "ADMIN".equals(u.getRole());
            String badgeClass = isAdmin ? "badge-admin" : "badge-receptionist";
        %>
        <div class="entity-card" style="cursor:pointer;"
             onclick="location.href='<%= ctx %>/control?action=userDetail&userId=<%= u.getUserId() %>'">
            <div class="ec-title"><%= u.getFullName() %> <span class="badge <%= badgeClass %>"><%= u.getRole() %></span></div>
            <div class="ec-row" style="color:#a9b6c0; font-size:.78rem; margin-top:-2px;">User ID: <%= u.getUserId() %></div>
            <div class="ec-row"><b>Username</b> <%= u.getUsername() %></div>
            <div class="ec-row">
                <b>Birth Year</b> <%= u.getBirthYear() != null ? u.getBirthYear() : "-" %>
                &nbsp;&middot;&nbsp; <b>Gender</b> <%= u.getGender() != null ? u.getGender() : "-" %>
            </div>
            <% if (!isAdmin) { %>
            <form method="post" action="<%= ctx %>/control" style="margin-top:10px;"
                  onclick="event.stopPropagation();"
                  onsubmit="return confirm('Delete this receptionist account? This cannot be undone.');">
                <input type="hidden" name="action" value="deleteUser">
                <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                <button type="submit" class="btn btn-sm" style="background:#c0392b;">Delete</button>
            </form>
            <% } %>
        </div>
        <% } %>
    </div>

    <div class="pagination">
        <% if (usersPage.hasPrevious()) { %>
            <a href="<%= pageLink.apply(usersPage.getCurrentPage() - 1) %>">&laquo; Prev</a>
        <% } else { %><span class="disabled">&laquo; Prev</span><% } %>
        <% for (int p = 1; p <= usersPage.getTotalPages(); p++) { %>
            <% if (p == usersPage.getCurrentPage()) { %><span class="current"><%= p %></span>
            <% } else { %><a href="<%= pageLink.apply(p) %>"><%= p %></a><% } %>
        <% } %>
        <% if (usersPage.hasNext()) { %>
            <a href="<%= pageLink.apply(usersPage.getCurrentPage() + 1) %>">Next &raquo;</a>
        <% } else { %><span class="disabled">Next &raquo;</span><% } %>
    </div>
    <% } %>
</div>

<%@ include file="includes/footer.jsp" %>

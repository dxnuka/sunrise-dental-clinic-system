<%@ page import="java.util.*" %>
<%
    String flashType = (String) session.getAttribute("flashType");
    String flashMessage = (String) session.getAttribute("flashMessage");
    // Flash pattern: read once, then clear immediately so a page refresh
    // doesn't re-show a stale message.
    session.removeAttribute("flashType");
    session.removeAttribute("flashMessage");
    if (flashMessage != null) {
        String cssClass = "alert-success";
        String icon = "&#10003;"; // check
        if ("error".equals(flashType)) { cssClass = "alert-error"; icon = "&#10007;"; }
        else if ("warning".equals(flashType)) { cssClass = "alert-warning"; icon = "&#9888;"; }
%>
        <div class="alert <%= cssClass %>"><span class="alert-icon"><%= icon %></span> <%= flashMessage %></div>
<%
    }
%>

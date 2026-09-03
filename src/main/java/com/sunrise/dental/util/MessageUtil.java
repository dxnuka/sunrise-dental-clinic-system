package com.sunrise.dental.util;

import jakarta.servlet.http.HttpSession;

/**
 * "Flash message" helper: stores a one-time success/error/warning message in
 * the session so it survives a redirect (Post-Redirect-Get), then the JSP
 * include (includes/messages.jsp) reads and immediately clears it.
 */
public class MessageUtil {

    public static void setSuccess(HttpSession session, String text) { set(session, "success", text); }
    public static void setError(HttpSession session, String text) { set(session, "error", text); }
    public static void setWarning(HttpSession session, String text) { set(session, "warning", text); }

    private static void set(HttpSession session, String type, String text) {
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", text);
    }
}

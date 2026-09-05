package com.sunrise.dental.util;

import jakarta.servlet.http.HttpSession;

public class MessageUtil {

    public static void setSuccess(HttpSession session, String text) { set(session, "success", text); }
    public static void setError(HttpSession session, String text) { set(session, "error", text); }
    public static void setWarning(HttpSession session, String text) { set(session, "warning", text); }

    private static void set(HttpSession session, String type, String text) {
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", text);
    }
}

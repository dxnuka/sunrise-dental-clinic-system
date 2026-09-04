package com.sunrise.dental.controller.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ---------------------------------------------------------------------------
 * DESIGN PATTERN: COMMAND (paired with FRONT CONTROLLER below)
 * ---------------------------------------------------------------------------
 * Each user action (login, register appointment, generate bill, view a
 * report...) is encapsulated as its own "command" object implementing this
 * interface. FrontControllerServlet holds a lookup table of these commands
 * and simply invokes handle() on whichever one matches the incoming action -
 * it never contains any action-specific logic itself. This keeps every
 * action independently testable and means adding a brand-new feature is
 * "write a new Handler class + register it", with zero changes to existing
 * classes (Open/Closed Principle).
 * ---------------------------------------------------------------------------
 */
public interface RequestHandler {
    /** @return the JSP path (relative to webapp root) to forward to after handling,
     *  or null if this handler already wrote the response directly (e.g. a PDF
     *  download or a JSON search result) and no forward should happen. */
    String handle(HttpServletRequest request, HttpServletResponse response) throws Exception;
}

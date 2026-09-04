package com.sunrise.dental.controller;

import com.sunrise.dental.controller.handler.*;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.MessageUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ---------------------------------------------------------------------------
 * DESIGN PATTERN: FRONT CONTROLLER
 * ---------------------------------------------------------------------------
 * A single servlet is the ONE entry point for every request (mapped to
 * /control). It is responsible for the cross-cutting concerns that would
 * otherwise be duplicated in every servlet: reading the "action" parameter,
 * enforcing "only authorized staff can use the system" (the login guard),
 * enforcing which actions are ADMIN-only (the role guard), catching any
 * stray exception so the user always sees a friendly message instead of a
 * stack trace, and finally forwarding to the right JSP view. The
 * action-specific work is delegated to small Command objects (see the
 * controller.handler package) looked up from the commandMap below.
 * ---------------------------------------------------------------------------
 */
@WebServlet(name = "FrontControllerServlet", urlPatterns = {"/control"})
public class FrontControllerServlet extends HttpServlet {

    private final Map<String, RequestHandler> commandMap = new HashMap<>();
    // Actions that are allowed WITHOUT being logged in:
    private static final String[] PUBLIC_ACTIONS = {"login", "register"};
    // Actions only an ADMIN-role user may reach - user management ("Add User" /
    // "Manage Users"). Receptionists are redirected away with a warning if they
    // try to navigate to one of these directly, not just hidden from the nav menu.
    private static final String[] ADMIN_ONLY_ACTIONS = {"addUserPage", "addUser", "users", "deleteUser", "userDetail"};

    @Override
    public void init() {
        commandMap.put("login", new LoginHandler());
        commandMap.put("logout", new LogoutHandler());
        commandMap.put("register", new RegisterHandler());
        commandMap.put("dashboard", new AppointmentsListHandler());
        commandMap.put("addAppointmentPage", new AddAppointmentPageHandler());
        commandMap.put("registerAppointment", new RegisterAppointmentHandler());
        commandMap.put("searchPatients", new SearchPatientsHandler());
        commandMap.put("availableSlots", new AvailableSlotsHandler());
        commandMap.put("viewAppointment", new ViewAppointmentHandler());
        commandMap.put("generateBill", new GenerateBillHandler());
        commandMap.put("updateAppointmentStatus", new UpdateAppointmentStatusHandler());
        commandMap.put("patients", new PatientsListHandler());
        commandMap.put("patientDetail", new PatientDetailHandler());
        commandMap.put("profile", new ProfileHandler());
        commandMap.put("updateProfile", new UpdateProfileHandler());
        commandMap.put("reports", new ReportsHandler());
        commandMap.put("generateReportPdf", new GenerateReportPdfHandler());
        commandMap.put("help", new HelpHandler());
        commandMap.put("addUserPage", new AddUserPageHandler());
        commandMap.put("addUser", new AddUserHandler());
        commandMap.put("users", new UsersListHandler());
        commandMap.put("userDetail", new UserDetailHandler());
        commandMap.put("deleteUser", new DeleteUserHandler());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        // Visiting the bare /control URL with no action (e.g. a browser refresh of a
        // page that was originally reached via a POSTed form, which shows just
        // "/control" in the address bar with no query string) should land somewhere
        // sensible rather than silently defaulting - redirect explicitly instead.
        if (action == null || action.trim().isEmpty()) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("loggedInUser") != null) {
                resp.sendRedirect(req.getContextPath() + "/control?action=dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
            }
            return;
        }

        // ---- Central authentication guard ("only authorized staff can use the system") ----
        User loggedInUser = null;
        if (!isPublic(action)) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("loggedInUser") == null) {
                MessageUtil.setWarning(req.getSession(), "Please log in to continue.");
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
                return;
            }
            loggedInUser = (User) session.getAttribute("loggedInUser");
        }

        // ---- Central role guard (user management is ADMIN-only) ----
        if (isAdminOnly(action) && (loggedInUser == null || !"ADMIN".equals(loggedInUser.getRole()))) {
            MessageUtil.setWarning(req.getSession(), "You do not have permission to access that page.");
            resp.sendRedirect(req.getContextPath() + "/control?action=dashboard");
            return;
        }

        RequestHandler handler = commandMap.get(action);
        String target;
        if (handler == null) {
            target = "error.jsp";
            req.setAttribute("errorMessage", "Unknown action: " + action);
        } else {
            try {
                target = handler.handle(req, resp);
            } catch (Exception e) {
                // Central error handling: every handler's exceptions land here so
                // the end user always gets a friendly message, never a raw stack trace.
                MessageUtil.setError(req.getSession(), "Something went wrong: " + e.getMessage());
                target = "error.jsp";
            }
        }
        // A null target means the handler already wrote the response itself
        // (e.g. a PDF download, a JSON search result, or a redirect) - nothing
        // left to forward.
        if (target != null) {
            req.getRequestDispatcher("/" + target).forward(req, resp);
        }
    }

    private boolean isPublic(String action) {
        for (String p : PUBLIC_ACTIONS) if (p.equals(action)) return true;
        return false;
    }

    private boolean isAdminOnly(String action) {
        for (String a : ADMIN_ONLY_ACTIONS) if (a.equals(action)) return true;
        return false;
    }
}

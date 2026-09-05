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


@WebServlet(name = "FrontControllerServlet", urlPatterns = {"/control"})
public class FrontControllerServlet extends HttpServlet {

    private final Map<String, RequestHandler> commandMap = new HashMap<>();

    private static final String[] PUBLIC_ACTIONS = {"login", "register"};
    private static final String[] ADMIN_ONLY_ACTIONS = {"addUserPage", "addUser", "users", "deleteUser", "userDetail",
            "dentistsPage", "addDentist", "deactivateDentist", "deletePatient"};

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
        commandMap.put("dentistsPage", new DentistsPageHandler());
        commandMap.put("addDentist", new AddDentistHandler());
        commandMap.put("deactivateDentist", new DeactivateDentistHandler());
        commandMap.put("deletePatient", new DeletePatientHandler());
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


        if (action == null || action.trim().isEmpty()) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("loggedInUser") != null) {
                resp.sendRedirect(req.getContextPath() + "/control?action=dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
            }
            return;
        }

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
                MessageUtil.setError(req.getSession(), "Something went wrong: " + e.getMessage());
                target = "error.jsp";
            }
        }

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

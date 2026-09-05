package com.sunrise.dental.factory;

import com.sunrise.dental.dao.*;
import com.sunrise.dental.dao.impl.*;

public class DAOFactory {

    private static final UserDAO USER_DAO = new UserDAOImpl();
    private static final DentistDAO DENTIST_DAO = new DentistDAOImpl();
    private static final TreatmentDAO TREATMENT_DAO = new TreatmentDAOImpl();
    private static final AppointmentDAO APPOINTMENT_DAO = new AppointmentDAOImpl();
    private static final BillDAO BILL_DAO = new BillDAOImpl();
    private static final ReportDAO REPORT_DAO = new ReportDAOImpl();
    private static final PatientDAO PATIENT_DAO = new PatientDAOImpl();

    private DAOFactory() {}

    public static UserDAO getUserDAO() { return USER_DAO; }
    public static DentistDAO getDentistDAO() { return DENTIST_DAO; }
    public static TreatmentDAO getTreatmentDAO() { return TREATMENT_DAO; }
    public static AppointmentDAO getAppointmentDAO() { return APPOINTMENT_DAO; }
    public static BillDAO getBillDAO() { return BILL_DAO; }
    public static ReportDAO getReportDAO() { return REPORT_DAO; }
    public static PatientDAO getPatientDAO() { return PATIENT_DAO; }
}

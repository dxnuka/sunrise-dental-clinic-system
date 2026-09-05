package com.sunrise.dental.service;

import com.sunrise.dental.dao.ReportDAO;
import com.sunrise.dental.factory.DAOFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportService {
    public static final int DEFAULT_RANGE_DAYS = 30;

    private final ReportDAO reportDAO;

    public ReportService() { this(DAOFactory.getReportDAO()); }
    public ReportService(ReportDAO reportDAO) { this.reportDAO = reportDAO; }

    public static LocalDate defaultFrom() { return LocalDate.now().minusDays(DEFAULT_RANGE_DAYS - 1); }
    public static LocalDate defaultTo() { return LocalDate.now(); }

    public List<Map<String, Object>> revenueByTreatment(LocalDate from, LocalDate to) {
        return reportDAO.revenueByTreatment(from, to);
    }

    public List<Map<String, Object>> dentistWorkload(LocalDate from, LocalDate to) {
        return reportDAO.dentistWorkload(from, to);
    }

    public List<Map<String, Object>> outstandingBills(LocalDate from, LocalDate to) {
        return reportDAO.outstandingBills(from, to);
    }
}

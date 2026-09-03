package com.sunrise.dental.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Backs the Reports screen; every method is filtered to a date range
 *  (revenue/outstanding by bill generation date, workload by appointment date). */
public interface ReportDAO {
    List<Map<String, Object>> revenueByTreatment(LocalDate from, LocalDate to);
    List<Map<String, Object>> dentistWorkload(LocalDate from, LocalDate to);
    List<Map<String, Object>> outstandingBills(LocalDate from, LocalDate to);
}

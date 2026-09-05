package com.sunrise.dental.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportDAO {
    List<Map<String, Object>> revenueByTreatment(LocalDate from, LocalDate to);
    List<Map<String, Object>> dentistWorkload(LocalDate from, LocalDate to);
    List<Map<String, Object>> outstandingBills(LocalDate from, LocalDate to);
}

package com.sunrise.dental.dao;

import java.time.LocalDate;

public class AppointmentFilter {
    private String searchTerm;
    private Integer dentistId;
    private Integer treatmentId;
    private String status;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String sortField = "date";
    private String sortDir = "asc";
    private int page = 1;
    private int pageSize = 9;

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public Integer getDentistId() { return dentistId; }
    public void setDentistId(Integer dentistId) { this.dentistId = dentistId; }
    public Integer getTreatmentId() { return treatmentId; }
    public void setTreatmentId(Integer treatmentId) { this.treatmentId = treatmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }
    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }
    public String getSortField() { return sortField; }
    public void setSortField(String sortField) { this.sortField = sortField; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(1, page); }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
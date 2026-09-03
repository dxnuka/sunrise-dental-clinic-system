package com.sunrise.dental.dao;

/**
 * Query-criteria object for the paginated appointments list (dashboard).
 * Kept as a plain parameter object (not a persisted entity) so
 * AppointmentDAO.findPaged() doesn't need a long, error-prone parameter list.
 */
public class AppointmentFilter {
    private String searchTerm;      // matches patient name, appointment number, dentist, or treatment
    private Integer dentistId;      // null = any dentist
    private Integer treatmentId;    // null = any treatment
    private String status;         // null = any status; else SCHEDULED/COMPLETED/CANCELLED
    private String sortField = "date"; // date | patient | dentist | treatment
    private String sortDir = "asc";    // asc | desc
    private int page = 1;               // 1-based
    private int pageSize = 9;

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public Integer getDentistId() { return dentistId; }
    public void setDentistId(Integer dentistId) { this.dentistId = dentistId; }
    public Integer getTreatmentId() { return treatmentId; }
    public void setTreatmentId(Integer treatmentId) { this.treatmentId = treatmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSortField() { return sortField; }
    public void setSortField(String sortField) { this.sortField = sortField; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(1, page); }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}

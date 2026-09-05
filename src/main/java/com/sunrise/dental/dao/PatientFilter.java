package com.sunrise.dental.dao;

public class PatientFilter {
    private String searchTerm;         // matches patient name or contact number
    private String sortField = "name"; // name | registered
    private String sortDir = "asc";
    private int page = 1;
    private int pageSize = 9;

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public String getSortField() { return sortField; }
    public void setSortField(String sortField) { this.sortField = sortField; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(1, page); }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}

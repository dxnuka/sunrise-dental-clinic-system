package com.sunrise.dental.model;

import java.util.Collections;
import java.util.List;

public class PageResult<T> {
    private final List<T> items;
    private final int currentPage;   // 1-based
    private final int pageSize;
    private final long totalItems;

    public PageResult(List<T> items, int currentPage, int pageSize, long totalItems) {
        this.items = items;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    public static <T> PageResult<T> empty(int page, int size) {
        return new PageResult<>(Collections.emptyList(), page, size, 0);
    }

    public List<T> getItems() { return items; }
    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return pageSize; }
    public long getTotalItems() { return totalItems; }
    public int getTotalPages() { return (int) Math.max(1, Math.ceil((double) totalItems / pageSize)); }
    public boolean hasPrevious() { return currentPage > 1; }
    public boolean hasNext() { return currentPage < getTotalPages(); }
}

package com.secondbrain.backend.inbox.api;

import java.util.List;

public class InboxPageResponse {

    private List<InboxItemResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public InboxPageResponse() {
    }

    public List<InboxItemResponse> getItems() {
        return items;
    }

    public void setItems(List<InboxItemResponse> items) {
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
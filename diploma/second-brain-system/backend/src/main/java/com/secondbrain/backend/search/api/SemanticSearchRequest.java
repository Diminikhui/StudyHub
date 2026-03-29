package com.secondbrain.backend.search.api;

public class SemanticSearchRequest {

    private String query;
    private int limit = 5;

    public SemanticSearchRequest() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getLimit() {
        return limit <= 0 ? 5 : limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
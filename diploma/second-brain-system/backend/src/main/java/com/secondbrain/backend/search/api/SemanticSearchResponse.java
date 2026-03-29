package com.secondbrain.backend.search.api;

import java.util.List;

public class SemanticSearchResponse {

    private String query;
    private int limit;
    private List<SemanticSearchResult> results;

    public SemanticSearchResponse() {
    }

    public SemanticSearchResponse(String query, int limit, List<SemanticSearchResult> results) {
        this.query = query;
        this.limit = limit;
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<SemanticSearchResult> getResults() {
        return results;
    }

    public void setResults(List<SemanticSearchResult> results) {
        this.results = results;
    }
}
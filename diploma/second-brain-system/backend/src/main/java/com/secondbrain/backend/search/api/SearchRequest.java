package com.secondbrain.backend.search.api;

import jakarta.validation.constraints.NotBlank;

public class SearchRequest {

    @NotBlank
    private String query;

    public SearchRequest() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
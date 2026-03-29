package com.secondbrain.backend.search.api;

import java.util.List;

public class UnifiedSemanticSearchResponse {

    private String query;
    private int limit;
    private List<SemanticSearchResult> topics;
    private List<SemanticSearchResult> facts;
    private List<SemanticSearchResult> actions;
    private List<SemanticSearchResult> all;

    public UnifiedSemanticSearchResponse() {
    }

    public UnifiedSemanticSearchResponse(
            String query,
            int limit,
            List<SemanticSearchResult> topics,
            List<SemanticSearchResult> facts,
            List<SemanticSearchResult> actions,
            List<SemanticSearchResult> all
    ) {
        this.query = query;
        this.limit = limit;
        this.topics = topics;
        this.facts = facts;
        this.actions = actions;
        this.all = all;
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

    public List<SemanticSearchResult> getTopics() {
        return topics;
    }

    public void setTopics(List<SemanticSearchResult> topics) {
        this.topics = topics;
    }

    public List<SemanticSearchResult> getFacts() {
        return facts;
    }

    public void setFacts(List<SemanticSearchResult> facts) {
        this.facts = facts;
    }

    public List<SemanticSearchResult> getActions() {
        return actions;
    }

    public void setActions(List<SemanticSearchResult> actions) {
        this.actions = actions;
    }

    public List<SemanticSearchResult> getAll() {
        return all;
    }

    public void setAll(List<SemanticSearchResult> all) {
        this.all = all;
    }
}
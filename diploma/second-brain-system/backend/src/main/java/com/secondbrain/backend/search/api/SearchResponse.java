package com.secondbrain.backend.search.api;

import com.secondbrain.backend.action.api.ActionItemResponse;
import com.secondbrain.backend.fact.api.FactResponse;
import com.secondbrain.backend.topic.api.TopicResponse;

import java.util.List;

public class SearchResponse {

    private String query;
    private List<FactResponse> facts;
    private List<ActionItemResponse> actions;
    private List<TopicResponse> topics;

    public SearchResponse() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<FactResponse> getFacts() {
        return facts;
    }

    public void setFacts(List<FactResponse> facts) {
        this.facts = facts;
    }

    public List<ActionItemResponse> getActions() {
        return actions;
    }

    public void setActions(List<ActionItemResponse> actions) {
        this.actions = actions;
    }

    public List<TopicResponse> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicResponse> topics) {
        this.topics = topics;
    }
}
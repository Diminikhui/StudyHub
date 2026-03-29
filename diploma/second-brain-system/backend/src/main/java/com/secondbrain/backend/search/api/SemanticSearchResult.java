package com.secondbrain.backend.search.api;

public class SemanticSearchResult {

    private Long embeddingId;
    private String entityType;
    private Long entityId;
    private String sourceText;
    private double distance;
    private String matchStrength;

    public SemanticSearchResult() {
    }

    public Long getEmbeddingId() {
        return embeddingId;
    }

    public void setEmbeddingId(Long embeddingId) {
        this.embeddingId = embeddingId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getMatchStrength() {
        return matchStrength;
    }

    public void setMatchStrength(String matchStrength) {
        this.matchStrength = matchStrength;
    }
}
package com.secondbrain.backend.openai.api;

import java.util.List;

public class EmbeddingDebugResponse {

    private String model;
    private int dimensions;
    private List<Double> preview;

    public EmbeddingDebugResponse() {
    }

    public EmbeddingDebugResponse(String model, int dimensions, List<Double> preview) {
        this.model = model;
        this.dimensions = dimensions;
        this.preview = preview;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public List<Double> getPreview() {
        return preview;
    }

    public void setPreview(List<Double> preview) {
        this.preview = preview;
    }
}
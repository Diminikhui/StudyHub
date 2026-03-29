package com.secondbrain.backend.openai.api;

public class EmbeddingRequest {

    private String text;

    public EmbeddingRequest() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
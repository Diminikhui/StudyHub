package com.secondbrain.backend.openai.client;

import java.util.List;

public class OpenAiEmbeddingResponse {

    private List<EmbeddingItem> data;

    public List<EmbeddingItem> getData() {
        return data;
    }

    public void setData(List<EmbeddingItem> data) {
        this.data = data;
    }

    public static class EmbeddingItem {
        private List<Double> embedding;

        public List<Double> getEmbedding() {
            return embedding;
        }

        public void setEmbedding(List<Double> embedding) {
            this.embedding = embedding;
        }
    }
}
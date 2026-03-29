package com.secondbrain.backend.openai;

import com.secondbrain.backend.openai.client.OpenAiEmbeddingResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class EmbeddingService {

    private final OpenAiProperties openAiProperties;
    private final RestClient restClient;

    public EmbeddingService(OpenAiProperties openAiProperties) {
        this.openAiProperties = openAiProperties;
        this.restClient = RestClient.builder()
                .baseUrl(openAiProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiProperties.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public List<Double> createEmbedding(String text) {
        if (text == null || text.isBlank()) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Text for embedding is blank");
        }

        if (openAiProperties.getApiKey() == null || openAiProperties.getApiKey().isBlank()) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "OPENAI_API_KEY is missing");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("input", text);
        body.put("model", openAiProperties.getEmbeddingModel());
        body.put("encoding_format", "float");

        OpenAiEmbeddingResponse response = restClient.post()
                .uri("/v1/embeddings")
                .body(body)
                .retrieve()
                .body(OpenAiEmbeddingResponse.class);

        if (response == null
                || response.getData() == null
                || response.getData().isEmpty()
                || response.getData().get(0).getEmbedding() == null) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "OpenAI returned empty embedding");
        }

        return response.getData().get(0).getEmbedding();
    }

    public String getModelName() {
        return openAiProperties.getEmbeddingModel();
    }
}
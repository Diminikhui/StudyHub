package com.secondbrain.backend.openai.api;

import com.secondbrain.backend.openai.EmbeddingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class EmbeddingDebugController {

    private final EmbeddingService embeddingService;

    public EmbeddingDebugController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping("/embedding")
    public EmbeddingDebugResponse createEmbedding(@RequestBody EmbeddingRequest request) {
        List<Double> embedding = embeddingService.createEmbedding(request.getText());

        List<Double> preview = embedding.stream()
                .limit(8)
                .toList();

        return new EmbeddingDebugResponse(
                embeddingService.getModelName(),
                embedding.size(),
                preview
        );
    }
}
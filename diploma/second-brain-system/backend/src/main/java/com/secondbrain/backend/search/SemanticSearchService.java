package com.secondbrain.backend.search;

import com.secondbrain.backend.embedding.KnowledgeEntityType;
import com.secondbrain.backend.openai.EmbeddingService;
import com.secondbrain.backend.search.api.SemanticSearchResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemanticSearchService {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;

    public SemanticSearchService(
            JdbcTemplate jdbcTemplate,
            EmbeddingService embeddingService
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
    }

    public List<SemanticSearchResult> searchTopics(String query, int limit) {
        return searchByEntityType(query, limit, KnowledgeEntityType.TOPIC);
    }

    public List<SemanticSearchResult> searchFacts(String query, int limit) {
        return searchByEntityType(query, limit, KnowledgeEntityType.FACT);
    }

    public List<SemanticSearchResult> searchActions(String query, int limit) {
        return searchByEntityType(query, limit, KnowledgeEntityType.ACTION);
    }

    public List<SemanticSearchResult> searchAll(String query, int limit) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        List<Double> queryVector = embeddingService.createEmbedding(query);
        String vectorLiteral = toVectorLiteral(queryVector);

        return jdbcTemplate.query(
                """
                select
                    id,
                    entity_type,
                    entity_id,
                    source_text,
                    embedding <=> CAST(? AS vector) as distance
                from knowledge_embedding
                order by embedding <=> CAST(? AS vector)
                limit ?
                """,
                (rs, rowNum) -> mapResult(
                        rs.getLong("id"),
                        rs.getString("entity_type"),
                        rs.getLong("entity_id"),
                        rs.getString("source_text"),
                        rs.getDouble("distance")
                ),
                vectorLiteral,
                vectorLiteral,
                normalizeLimit(limit)
        );
    }

    private List<SemanticSearchResult> searchByEntityType(String query, int limit, KnowledgeEntityType entityType) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        List<Double> queryVector = embeddingService.createEmbedding(query);
        String vectorLiteral = toVectorLiteral(queryVector);

        return jdbcTemplate.query(
                """
                select
                    id,
                    entity_type,
                    entity_id,
                    source_text,
                    embedding <=> CAST(? AS vector) as distance
                from knowledge_embedding
                where entity_type = ?
                order by embedding <=> CAST(? AS vector)
                limit ?
                """,
                (rs, rowNum) -> mapResult(
                        rs.getLong("id"),
                        rs.getString("entity_type"),
                        rs.getLong("entity_id"),
                        rs.getString("source_text"),
                        rs.getDouble("distance")
                ),
                vectorLiteral,
                entityType.name(),
                vectorLiteral,
                normalizeLimit(limit)
        );
    }

    private SemanticSearchResult mapResult(
            Long embeddingId,
            String entityType,
            Long entityId,
            String sourceText,
            double distance
    ) {
        SemanticSearchResult result = new SemanticSearchResult();
        result.setEmbeddingId(embeddingId);
        result.setEntityType(entityType);
        result.setEntityId(entityId);
        result.setSourceText(sourceText);
        result.setDistance(distance);
        result.setMatchStrength(classifyMatchStrength(distance));
        return result;
    }

    private String classifyMatchStrength(double distance) {
        if (distance <= 0.35) {
            return "STRONG";
        }
        if (distance <= 0.65) {
            return "MEDIUM";
        }
        return "WEAK";
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return 5;
        }
        return Math.min(limit, 20);
    }

    private String toVectorLiteral(List<Double> vector) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < vector.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(vector.get(i));
        }

        sb.append("]");
        return sb.toString();
    }
}
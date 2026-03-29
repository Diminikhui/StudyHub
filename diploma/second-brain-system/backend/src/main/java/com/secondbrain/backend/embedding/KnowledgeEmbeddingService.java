package com.secondbrain.backend.embedding;

import com.secondbrain.backend.openai.EmbeddingService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class KnowledgeEmbeddingService {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;

    public KnowledgeEmbeddingService(
            JdbcTemplate jdbcTemplate,
            EmbeddingService embeddingService
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
    }

    public void upsert(KnowledgeEntityType entityType, Long entityId, String sourceText) {
        if (entityId == null || sourceText == null || sourceText.isBlank()) {
            return;
        }

        List<Double> vector = embeddingService.createEmbedding(sourceText);
        String vectorLiteral = toVectorLiteral(vector);

        Long existingId = jdbcTemplate.query(
                """
                select id
                from knowledge_embedding
                where entity_type = ? and entity_id = ?
                """,
                rs -> rs.next() ? rs.getLong("id") : null,
                entityType.name(),
                entityId
        );

        LocalDateTime now = LocalDateTime.now();

        if (existingId == null) {
            jdbcTemplate.update(
                    """
                    insert into knowledge_embedding (
                        entity_type,
                        entity_id,
                        source_text,
                        embedding,
                        created_at,
                        updated_at
                    )
                    values (?, ?, ?, CAST(? AS vector), ?, ?)
                    """,
                    entityType.name(),
                    entityId,
                    sourceText.trim(),
                    vectorLiteral,
                    Timestamp.valueOf(now),
                    Timestamp.valueOf(now)
            );
        } else {
            jdbcTemplate.update(
                    """
                    update knowledge_embedding
                    set source_text = ?,
                        embedding = CAST(? AS vector),
                        updated_at = ?
                    where id = ?
                    """,
                    sourceText.trim(),
                    vectorLiteral,
                    Timestamp.valueOf(now),
                    existingId
            );
        }
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
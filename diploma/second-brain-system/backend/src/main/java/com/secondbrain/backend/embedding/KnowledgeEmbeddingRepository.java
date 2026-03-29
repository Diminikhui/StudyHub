package com.secondbrain.backend.embedding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KnowledgeEmbeddingRepository extends JpaRepository<KnowledgeEmbedding, Long> {

    Optional<KnowledgeEmbedding> findByEntityTypeAndEntityId(KnowledgeEntityType entityType, Long entityId);
}

package com.secondbrain.backend.topic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByNormalizedName(String normalizedName);

    List<Topic> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String query);

    List<Topic> findByNormalizedNameContainingIgnoreCaseOrderByCreatedAtDesc(String query);
}
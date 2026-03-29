package com.secondbrain.backend.fact;

import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.topic.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FactRepository extends JpaRepository<Fact, Long> {

    List<Fact> findByRawItemOrderByCreatedAtAsc(RawItem rawItem);

    List<Fact> findByContentTextContainingIgnoreCaseOrderByCreatedAtDesc(String query);

    List<Fact> findByTopicOrderByCreatedAtAsc(Topic topic);
}
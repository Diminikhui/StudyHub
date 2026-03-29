package com.secondbrain.backend.action;

import com.secondbrain.backend.person.Person;
import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.topic.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    List<ActionItem> findByRawItemOrderByCreatedAtAsc(RawItem rawItem);

    Optional<ActionItem> findByRawItemAndTitle(RawItem rawItem, String title);

    List<ActionItem> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

    List<ActionItem> findByTopicOrderByCreatedAtAsc(Topic topic);

    List<ActionItem> findByPersonOrderByCreatedAtAsc(Person person);
}
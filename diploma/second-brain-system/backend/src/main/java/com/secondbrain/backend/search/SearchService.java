package com.secondbrain.backend.search;

import com.secondbrain.backend.action.ActionItem;
import com.secondbrain.backend.action.ActionItemRepository;
import com.secondbrain.backend.action.api.ActionItemResponse;
import com.secondbrain.backend.fact.Fact;
import com.secondbrain.backend.fact.FactRepository;
import com.secondbrain.backend.fact.api.FactResponse;
import com.secondbrain.backend.search.api.SearchRequest;
import com.secondbrain.backend.search.api.SearchResponse;
import com.secondbrain.backend.topic.Topic;
import com.secondbrain.backend.topic.TopicRepository;
import com.secondbrain.backend.topic.api.TopicResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private final FactRepository factRepository;
    private final ActionItemRepository actionItemRepository;
    private final TopicRepository topicRepository;

    public SearchService(
            FactRepository factRepository,
            ActionItemRepository actionItemRepository,
            TopicRepository topicRepository
    ) {
        this.factRepository = factRepository;
        this.actionItemRepository = actionItemRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional(readOnly = true)
    public SearchResponse search(SearchRequest request) {
        String query = request.getQuery().trim();

        List<FactResponse> facts = factRepository
                .findByContentTextContainingIgnoreCaseOrderByCreatedAtDesc(query)
                .stream()
                .map(this::toFactResponse)
                .toList();

        List<ActionItemResponse> actions = actionItemRepository
                .findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(query)
                .stream()
                .map(this::toActionResponse)
                .toList();

        Map<Long, Topic> uniqueTopics = new LinkedHashMap<>();

        for (Topic topic : topicRepository.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(query)) {
            uniqueTopics.put(topic.getId(), topic);
        }
        for (Topic topic : topicRepository.findByNormalizedNameContainingIgnoreCaseOrderByCreatedAtDesc(query)) {
            uniqueTopics.put(topic.getId(), topic);
        }

        List<TopicResponse> topics = uniqueTopics.values()
                .stream()
                .map(this::toTopicResponse)
                .toList();

        SearchResponse response = new SearchResponse();
        response.setQuery(query);
        response.setFacts(facts);
        response.setActions(actions);
        response.setTopics(topics);
        return response;
    }

    private FactResponse toFactResponse(Fact fact) {
        FactResponse response = new FactResponse();
        response.setId(fact.getId());
        response.setContentText(fact.getContentText());

        if (fact.getTopic() != null) {
            response.setTopicId(fact.getTopic().getId());
            response.setTopicName(fact.getTopic().getName());
        }

        response.setCreatedAt(fact.getCreatedAt());
        response.setUpdatedAt(fact.getUpdatedAt());
        return response;
    }

    private ActionItemResponse toActionResponse(ActionItem actionItem) {
        ActionItemResponse response = new ActionItemResponse();
        response.setId(actionItem.getId());
        response.setTitle(actionItem.getTitle());
        response.setDone(actionItem.isDone());
        response.setCreatedAt(actionItem.getCreatedAt());
        response.setUpdatedAt(actionItem.getUpdatedAt());
        return response;
    }

    private TopicResponse toTopicResponse(Topic topic) {
        TopicResponse response = new TopicResponse();
        response.setId(topic.getId());
        response.setName(topic.getName());
        response.setNormalizedName(topic.getNormalizedName());
        response.setCreatedAt(topic.getCreatedAt());
        response.setUpdatedAt(topic.getUpdatedAt());
        return response;
    }
}
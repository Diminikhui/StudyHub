package com.secondbrain.backend.topic;

import com.secondbrain.backend.fact.Fact;
import com.secondbrain.backend.fact.FactRepository;
import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.raw.RawItemNotFoundException;
import com.secondbrain.backend.raw.RawItemRepository;
import com.secondbrain.backend.topic.api.TopicResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TopicQueryService {

    private final RawItemRepository rawItemRepository;
    private final FactRepository factRepository;

    public TopicQueryService(
            RawItemRepository rawItemRepository,
            FactRepository factRepository
    ) {
        this.rawItemRepository = rawItemRepository;
        this.factRepository = factRepository;
    }

    @Transactional(readOnly = true)
    public List<TopicResponse> getByRawItemId(UUID rawItemId) {
        RawItem rawItem = rawItemRepository.findById(rawItemId)
                .orElseThrow(() -> new RawItemNotFoundException(rawItemId));

        return factRepository.findByRawItemOrderByCreatedAtAsc(rawItem)
                .stream()
                .map(Fact::getTopic)
                .filter(topic -> topic != null)
                .distinct()
                .map(this::toResponse)
                .toList();
    }

    private TopicResponse toResponse(Topic topic) {
        TopicResponse response = new TopicResponse();
        response.setId(topic.getId());
        response.setName(topic.getName());
        response.setNormalizedName(topic.getNormalizedName());
        response.setCreatedAt(topic.getCreatedAt());
        response.setUpdatedAt(topic.getUpdatedAt());
        return response;
    }
}
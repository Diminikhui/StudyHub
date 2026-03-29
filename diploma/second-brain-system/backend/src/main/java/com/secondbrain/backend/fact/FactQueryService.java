package com.secondbrain.backend.fact;

import com.secondbrain.backend.fact.api.FactResponse;
import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.raw.RawItemNotFoundException;
import com.secondbrain.backend.raw.RawItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FactQueryService {

    private final RawItemRepository rawItemRepository;
    private final FactRepository factRepository;

    public FactQueryService(
            RawItemRepository rawItemRepository,
            FactRepository factRepository
    ) {
        this.rawItemRepository = rawItemRepository;
        this.factRepository = factRepository;
    }

    @Transactional(readOnly = true)
    public List<FactResponse> getByRawItemId(UUID rawItemId) {
        RawItem rawItem = rawItemRepository.findById(rawItemId)
                .orElseThrow(() -> new RawItemNotFoundException(rawItemId));

        return factRepository.findByRawItemOrderByCreatedAtAsc(rawItem)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private FactResponse toResponse(Fact fact) {
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
}
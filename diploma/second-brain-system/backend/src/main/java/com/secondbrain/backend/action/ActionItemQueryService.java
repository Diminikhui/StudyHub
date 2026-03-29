package com.secondbrain.backend.action;

import com.secondbrain.backend.action.api.ActionItemResponse;
import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.raw.RawItemNotFoundException;
import com.secondbrain.backend.raw.RawItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ActionItemQueryService {

    private final RawItemRepository rawItemRepository;
    private final ActionItemRepository actionItemRepository;

    public ActionItemQueryService(
            RawItemRepository rawItemRepository,
            ActionItemRepository actionItemRepository
    ) {
        this.rawItemRepository = rawItemRepository;
        this.actionItemRepository = actionItemRepository;
    }

    @Transactional(readOnly = true)
    public List<ActionItemResponse> getByRawItemId(UUID rawItemId) {
        RawItem rawItem = rawItemRepository.findById(rawItemId)
                .orElseThrow(() -> new RawItemNotFoundException(rawItemId));

        return actionItemRepository.findByRawItemOrderByCreatedAtAsc(rawItem)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ActionItemResponse toResponse(ActionItem actionItem) {
        ActionItemResponse response = new ActionItemResponse();
        response.setId(actionItem.getId());
        response.setTitle(actionItem.getTitle());
        response.setDone(actionItem.isDone());

        if (actionItem.getTopic() != null) {
            response.setTopicId(actionItem.getTopic().getId());
            response.setTopicName(actionItem.getTopic().getName());
        }

        if (actionItem.getPerson() != null) {
            response.setPersonId(actionItem.getPerson().getId());
            response.setPersonName(actionItem.getPerson().getDisplayName());
        }

        response.setCreatedAt(actionItem.getCreatedAt());
        response.setUpdatedAt(actionItem.getUpdatedAt());
        return response;
    }
}
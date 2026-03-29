package com.secondbrain.backend.action;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ActionItemCommandService {

    private final ActionItemRepository actionItemRepository;

    public ActionItemCommandService(ActionItemRepository actionItemRepository) {
        this.actionItemRepository = actionItemRepository;
    }

    public void markDone(Long actionItemId) {
        ActionItem actionItem = actionItemRepository.findById(actionItemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Action item not found: " + actionItemId
                ));

        actionItem.setDone(true);
        actionItem.setUpdatedAt(LocalDateTime.now());

        actionItemRepository.save(actionItem);
    }
}
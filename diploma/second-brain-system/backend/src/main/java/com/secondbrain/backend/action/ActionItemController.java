package com.secondbrain.backend.action.api;

import com.secondbrain.backend.action.ActionItemCommandService;
import com.secondbrain.backend.action.ActionItemQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class ActionItemController {

    private final ActionItemQueryService actionItemQueryService;
    private final ActionItemCommandService actionItemCommandService;

    public ActionItemController(
            ActionItemQueryService actionItemQueryService,
            ActionItemCommandService actionItemCommandService
    ) {
        this.actionItemQueryService = actionItemQueryService;
        this.actionItemCommandService = actionItemCommandService;
    }

    @GetMapping("/api/raw-items/{rawItemId}/actions")
    public List<ActionItemResponse> getByRawItemId(@PathVariable UUID rawItemId) {
        return actionItemQueryService.getByRawItemId(rawItemId);
    }

    @PostMapping("/api/actions/{actionItemId}/done")
    public void markDone(@PathVariable Long actionItemId) {
        actionItemCommandService.markDone(actionItemId);
    }
}
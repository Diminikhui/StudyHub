package com.secondbrain.backend.topic.api;

import com.secondbrain.backend.topic.TopicQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class TopicController {

    private final TopicQueryService topicQueryService;

    public TopicController(TopicQueryService topicQueryService) {
        this.topicQueryService = topicQueryService;
    }

    @GetMapping("/api/raw-items/{rawItemId}/topics")
    public List<TopicResponse> getByRawItemId(@PathVariable UUID rawItemId) {
        return topicQueryService.getByRawItemId(rawItemId);
    }
}
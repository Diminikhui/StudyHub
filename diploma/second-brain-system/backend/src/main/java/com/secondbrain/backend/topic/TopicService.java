package com.secondbrain.backend.topic;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicNormalizationService topicNormalizationService;

    public TopicService(
            TopicRepository topicRepository,
            TopicNormalizationService topicNormalizationService
    ) {
        this.topicRepository = topicRepository;
        this.topicNormalizationService = topicNormalizationService;
    }

    public Topic findOrCreate(String topicName) {
        String normalizedName = topicNormalizationService.normalize(topicName);

        return topicRepository.findByNormalizedName(normalizedName)
                .orElseGet(() -> {
                    Topic topic = new Topic();
                    topic.setName(topicName.trim());
                    topic.setNormalizedName(normalizedName);
                    topic.setCreatedAt(LocalDateTime.now());
                    topic.setUpdatedAt(LocalDateTime.now());
                    return topicRepository.save(topic);
                });
    }
}
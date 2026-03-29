package com.secondbrain.backend.topic;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class TopicNormalizationService {

    public String normalize(String input) {
        if (input == null) {
            return "";
        }

        return input
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
}
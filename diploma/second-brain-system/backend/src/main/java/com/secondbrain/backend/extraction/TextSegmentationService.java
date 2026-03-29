package com.secondbrain.backend.extraction;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextSegmentationService {

    public List<String> segment(String contentText) {
        if (contentText == null || contentText.isBlank()) {
            return List.of();
        }

        String normalized = contentText
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim();

        String[] roughParts = normalized.split("(?<=[.!?])\\s+|\\n+|;");

        List<String> result = new ArrayList<>();
        for (String part : roughParts) {
            String cleaned = clean(part);
            if (!cleaned.isBlank()) {
                result.add(cleaned);
            }
        }

        return result;
    }

    private String clean(String text) {
        return text == null
                ? ""
                : text.trim().replaceAll("\\s+", " ");
    }
}
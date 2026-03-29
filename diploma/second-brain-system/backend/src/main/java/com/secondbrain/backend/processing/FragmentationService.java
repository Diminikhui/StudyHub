package com.secondbrain.backend.processing;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FragmentationService {

    public List<String> fragment(String normalizedText) {
        if (normalizedText == null || normalizedText.isBlank()) {
            return List.of();
        }

        return List.of(normalizedText);
    }
}
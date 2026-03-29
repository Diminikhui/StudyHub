package com.secondbrain.backend.processing;

import org.springframework.stereotype.Service;

@Service
public class NormalizationService {

    public String normalize(String contentText) {
        if (contentText == null) {
            return "";
        }

        return contentText
                .trim()
                .replaceAll("\\s+", " ");
    }
}
package com.secondbrain.backend.extraction;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClauseSegmentationService {

    public List<String> splitClauses(String segment) {
        if (segment == null || segment.isBlank()) {
            return List.of();
        }

        String normalized = normalize(segment);

        String[] strongParts = normalized.split(
                "\\s*,\\s*а потом\\s+|\\s*,\\s*потом\\s+|\\s*,\\s*затем\\s+|\\s*,\\s*ещ[её]\\s+|\\s*,\\s*а также\\s+"
        );

        List<String> result = new ArrayList<>();
        for (String strongPart : strongParts) {
            result.addAll(splitInnerAnd(strongPart));
        }

        List<String> cleaned = new ArrayList<>();
        for (String item : result) {
            String value = cleanClause(item);
            if (!value.isBlank()) {
                cleaned.add(value);
            }
        }

        return cleaned;
    }

    private List<String> splitInnerAnd(String text) {
        String normalized = normalize(text);

        if (!looksLikeActionList(normalized)) {
            return List.of(normalized);
        }

        String[] parts = normalized.split("\\s+и\\s+");
        List<String> result = new ArrayList<>();

        String carryPrefix = extractActionPrefix(normalized);

        for (int i = 0; i < parts.length; i++) {
            String part = cleanClause(parts[i]);
            if (part.isBlank()) {
                continue;
            }

            if (i == 0) {
                result.add(part);
                continue;
            }

            if (startsWithVerbLike(part)) {
                if (!carryPrefix.isBlank() && !startsWithActionPrefix(part)) {
                    result.add(carryPrefix + " " + decapitalize(part));
                } else {
                    result.add(part);
                }
            } else if (!carryPrefix.isBlank()) {
                result.add(carryPrefix + " " + decapitalize(part));
            } else {
                result.add(part);
            }
        }

        return result;
    }

    private boolean looksLikeActionList(String text) {
        String lower = text.toLowerCase();
        return lower.contains("нужно ")
                || lower.contains("надо ")
                || lower.contains("сделать ")
                || lower.contains("купить ")
                || lower.contains("записать ")
                || lower.contains("обсудить ")
                || lower.contains("позвонить ");
    }

    private String extractActionPrefix(String text) {
        String lower = text.toLowerCase();

        if (lower.startsWith("нужно ")) {
            return "Нужно";
        }
        if (lower.startsWith("надо ")) {
            return "Надо";
        }

        return "";
    }

    private boolean startsWithVerbLike(String text) {
        String lower = text.toLowerCase();
        return lower.startsWith("купить ")
                || lower.startsWith("записать ")
                || lower.startsWith("обсудить ")
                || lower.startsWith("позвонить ")
                || lower.startsWith("сделать ");
    }

    private boolean startsWithActionPrefix(String text) {
        String lower = text.toLowerCase();
        return lower.startsWith("нужно ") || lower.startsWith("надо ");
    }

    private String cleanClause(String text) {
        String normalized = normalize(text);
        normalized = normalized.replaceAll("[.]+$", "");
        normalized = normalized.replaceAll("\\s+", " ").trim();
        return normalized;
    }

    private String decapitalize(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text.substring(0, 1).toLowerCase() + text.substring(1);
    }

    private String normalize(String text) {
        return text == null ? "" : text.trim().replaceAll("\\s+", " ");
    }
}
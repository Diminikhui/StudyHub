package com.secondbrain.backend.extraction;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MeaningExtractionService {

    private static final Pattern PERSON_WITH_S_PATTERN =
            Pattern.compile("(?:^|\\s)с\\s+([А-ЯЁA-Z][а-яёa-z]+)(?=$|[\\s,.!?;:])");

    private final TextSegmentationService textSegmentationService;
    private final ClauseSegmentationService clauseSegmentationService;

    public MeaningExtractionService(
            TextSegmentationService textSegmentationService,
            ClauseSegmentationService clauseSegmentationService
    ) {
        this.textSegmentationService = textSegmentationService;
        this.clauseSegmentationService = clauseSegmentationService;
    }

    public List<MeaningUnit> extract(String contentText) {
        List<String> segments = textSegmentationService.segment(contentText);
        List<MeaningUnit> result = new ArrayList<>();

        int order = 0;
        for (String segment : segments) {
            List<String> clauses = clauseSegmentationService.splitClauses(segment);

            for (String clause : clauses) {
                List<MeaningUnit> units = extractFromClause(clause, order);
                result.addAll(units);
                order++;
            }
        }

        return result;
    }

    private List<MeaningUnit> extractFromClause(String clause, int sourceOrder) {
        List<MeaningUnit> units = new ArrayList<>();
        String lower = clause.toLowerCase();

        boolean isAction = lower.contains("надо")
                || lower.contains("нужно")
                || lower.contains("сделать")
                || lower.contains("купить")
                || lower.contains("позвонить")
                || lower.contains("записать")
                || lower.contains("обсудить");

        boolean isExplicitTopic = lower.startsWith("тема:")
                || lower.startsWith("topic:");

        boolean isTopicContext = lower.startsWith("идея продукта:")
                || lower.startsWith("идея:")
                || lower.startsWith("проект:")
                || lower.startsWith("контекст:")
                || lower.startsWith("про ")
                || lower.startsWith("о ")
                || lower.startsWith("об ");

        if (isAction) {
            units.add(new MeaningUnit(clause, MeaningUnitType.ACTION, 100, sourceOrder, clause));
        } else {
            units.add(new MeaningUnit(clause, MeaningUnitType.FACT, 80, sourceOrder, clause));
        }

        if (isExplicitTopic || isTopicContext || lower.contains("второй мозг")) {
            String topicText = extractTopicText(clause);
            if (!topicText.isBlank()) {
                units.add(new MeaningUnit(topicText, MeaningUnitType.TOPIC, 90, sourceOrder, clause));
            }
        }

        String personName = extractPersonName(clause);
        if (!personName.isBlank()) {
            units.add(new MeaningUnit(personName, MeaningUnitType.PERSON, 95, sourceOrder, clause));
        }

        return units;
    }

    private String extractTopicText(String clause) {
        String normalized = clause == null
                ? ""
                : clause.trim().replaceAll("\\s+", " ");

        String lower = normalized.toLowerCase();

        if (lower.startsWith("тема:")) {
            return normalized.substring(5).trim();
        }

        if (lower.startsWith("topic:")) {
            return normalized.substring(6).trim();
        }

        if (lower.startsWith("идея продукта:")) {
            return "Второй мозг";
        }

        if (lower.startsWith("идея:")) {
            return normalized.substring(5).trim();
        }

        if (lower.startsWith("проект:")) {
            return normalized.substring(7).trim();
        }

        if (lower.startsWith("контекст:")) {
            return normalized.substring(9).trim();
        }

        if (lower.contains("второй мозг")) {
            return "Второй мозг";
        }

        return normalized;
    }

    private String extractPersonName(String clause) {
        if (clause == null || clause.isBlank()) {
            return "";
        }

        String normalized = clause.trim().replaceAll("\\s+", " ");
        Matcher matcher = PERSON_WITH_S_PATTERN.matcher(normalized);
        if (matcher.find()) {
            String rawName = matcher.group(1).trim();
            return normalizeRussianPersonForm(rawName);
        }

        return "";
    }

    private String normalizeRussianPersonForm(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return "";
        }

        String name = rawName.trim();

        if (name.length() < 3) {
            return name;
        }

        if (name.endsWith("ой")) {
            return name.substring(0, name.length() - 2) + "а";
        }

        if (name.endsWith("ей")) {
            return name.substring(0, name.length() - 2) + "я";
        }

        if (name.endsWith("ёй")) {
            return name.substring(0, name.length() - 2) + "я";
        }

        if (name.endsWith("ем")) {
            return name.substring(0, name.length() - 2) + "й";
        }

        return name;
    }
}
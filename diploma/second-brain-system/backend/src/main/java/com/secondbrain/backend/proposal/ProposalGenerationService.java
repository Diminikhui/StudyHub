package com.secondbrain.backend.proposal;

import com.secondbrain.backend.extraction.MeaningExtractionService;
import com.secondbrain.backend.extraction.MeaningUnit;
import com.secondbrain.backend.extraction.MeaningUnitType;
import com.secondbrain.backend.raw.RawItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProposalGenerationService {

    private final ProposalRepository proposalRepository;
    private final MeaningExtractionService meaningExtractionService;

    public ProposalGenerationService(
            ProposalRepository proposalRepository,
            MeaningExtractionService meaningExtractionService
    ) {
        this.proposalRepository = proposalRepository;
        this.meaningExtractionService = meaningExtractionService;
    }

    public void generateForRawItem(RawItem rawItem) {
        List<MeaningUnit> meaningUnits = meaningExtractionService.extract(rawItem.getContentText());
        Set<String> dedupKeys = new LinkedHashSet<>();

        for (MeaningUnit unit : meaningUnits) {
            ProposalType proposalType = mapType(unit.getType());
            String title = buildTitle(unit);

            if (title.isBlank()) {
                continue;
            }

            String dedupKey = proposalType.name() + "::" + title.toLowerCase().trim();
            if (!dedupKeys.add(dedupKey)) {
                continue;
            }

            Proposal proposal = new Proposal();
            proposal.setRawItem(rawItem);
            proposal.setProposalType(proposalType);
            proposal.setStatus(ProposalStatus.PENDING);
            proposal.setTitle(title);
            proposal.setDescription(buildDescription(proposalType, unit));
            proposal.setPayloadJson(buildPayloadJson(unit));
            proposal.setCreatedAt(LocalDateTime.now());
            proposal.setUpdatedAt(LocalDateTime.now());

            proposalRepository.save(proposal);
        }

        if (dedupKeys.isEmpty()) {
            Proposal fallback = new Proposal();
            fallback.setRawItem(rawItem);
            fallback.setProposalType(ProposalType.FACT_CANDIDATE);
            fallback.setStatus(ProposalStatus.PENDING);
            fallback.setTitle(buildFallbackTitle(rawItem.getContentText()));
            fallback.setDescription("Fallback fact proposal from raw item");
            fallback.setPayloadJson(null);
            fallback.setCreatedAt(LocalDateTime.now());
            fallback.setUpdatedAt(LocalDateTime.now());

            proposalRepository.save(fallback);
        }
    }

    private ProposalType mapType(MeaningUnitType type) {
        return switch (type) {
            case ACTION -> ProposalType.ACTION_CANDIDATE;
            case FACT -> ProposalType.FACT_CANDIDATE;
            case TOPIC -> ProposalType.TOPIC_CANDIDATE;
            case PERSON -> ProposalType.PERSON_CANDIDATE;
        };
    }

    private String buildTitle(MeaningUnit unit) {
        String normalized = unit.getText() == null
                ? ""
                : unit.getText().trim().replaceAll("\\s+", " ");

        normalized = normalized.replaceAll("[.]+$", "").trim();

        if (normalized.isBlank()) {
            return "";
        }

        if (unit.getType() == MeaningUnitType.ACTION) {
            normalized = uppercaseFirst(normalized);
        }

        return limit(normalized, 120);
    }

    private String buildFallbackTitle(String contentText) {
        String normalized = contentText == null
                ? ""
                : contentText.trim().replaceAll("\\s+", " ");

        normalized = normalized.replaceAll("[.]+$", "").trim();

        if (normalized.isBlank()) {
            return "Untitled proposal";
        }

        return limit(normalized, 120);
    }

    private String buildDescription(ProposalType proposalType, MeaningUnit unit) {
        String base = switch (proposalType) {
            case ACTION_CANDIDATE -> "Extracted action proposal from meaning unit";
            case FACT_CANDIDATE -> "Extracted fact proposal from meaning unit";
            case TOPIC_CANDIDATE -> "Extracted topic proposal from meaning unit";
            case PERSON_CANDIDATE -> "Extracted person proposal from meaning unit";
        };

        return base + " [priority=" + unit.getPriority() + ", order=" + unit.getSourceOrder() + "]";
    }

    private String buildPayloadJson(MeaningUnit unit) {
        String clauseText = unit.getClauseText() == null
                ? ""
                : unit.getClauseText().replace("\"", "\\\"");

        return "{\"clauseText\":\"" + clauseText + "\",\"sourceOrder\":" + unit.getSourceOrder() + "}";
    }

    private String uppercaseFirst(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String limit(String value, int maxLength) {
        return value.length() <= maxLength
                ? value
                : value.substring(0, maxLength);
    }
}
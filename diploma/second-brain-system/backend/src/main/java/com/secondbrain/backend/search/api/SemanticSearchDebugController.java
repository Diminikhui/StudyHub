package com.secondbrain.backend.search.api;

import com.secondbrain.backend.search.SemanticSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SemanticSearchDebugController {

    private final SemanticSearchService semanticSearchService;

    public SemanticSearchDebugController(SemanticSearchService semanticSearchService) {
        this.semanticSearchService = semanticSearchService;
    }

    @PostMapping("/api/debug/search/topics")
    public SemanticSearchResponse searchTopics(@RequestBody SemanticSearchRequest request) {
        List<SemanticSearchResult> results = semanticSearchService.searchTopics(
                request.getQuery(),
                request.getLimit()
        );

        return new SemanticSearchResponse(
                request.getQuery(),
                request.getLimit(),
                results
        );
    }

    @PostMapping("/api/debug/search/all")
    public UnifiedSemanticSearchResponse searchAll(@RequestBody SemanticSearchRequest request) {
        List<SemanticSearchResult> topics = semanticSearchService.searchTopics(
                request.getQuery(),
                request.getLimit()
        );

        List<SemanticSearchResult> facts = semanticSearchService.searchFacts(
                request.getQuery(),
                request.getLimit()
        );

        List<SemanticSearchResult> actions = semanticSearchService.searchActions(
                request.getQuery(),
                request.getLimit()
        );

        List<SemanticSearchResult> all = semanticSearchService.searchAll(
                request.getQuery(),
                request.getLimit()
        );

        return new UnifiedSemanticSearchResponse(
                request.getQuery(),
                request.getLimit(),
                topics,
                facts,
                actions,
                all
        );
    }

    @PostMapping("/api/answer")
    public GroundedAnswerResponse answer(@RequestBody SemanticSearchRequest request) {
        List<SemanticSearchResult> all = semanticSearchService.searchAll(
                request.getQuery(),
                request.getLimit()
        );

        List<SemanticSearchResult> usable = all.stream()
                .filter(result -> !"WEAK".equals(result.getMatchStrength()))
                .toList();

        if (usable.isEmpty()) {
            return new GroundedAnswerResponse(
                    "NOT_FOUND",
                    "По запросу не найдено достаточно надёжных материалов.",
                    List.of()
            );
        }

        SemanticSearchResult bestTopic = firstOfType(usable, "TOPIC");
        SemanticSearchResult bestFact = firstOfType(usable, "FACT");
        SemanticSearchResult bestAction = firstOfType(usable, "ACTION");

        StringBuilder answer = new StringBuilder();

        if (bestTopic != null) {
            answer.append("Найдена тема «")
                    .append(bestTopic.getSourceText())
                    .append("».");
        }

        if (bestFact != null) {
            if (!answer.isEmpty()) {
                answer.append(" ");
            }
            answer.append("По найденным материалам: ")
                    .append(bestFact.getSourceText())
                    .append(".");
        }

        if (bestAction != null) {
            if (!answer.isEmpty()) {
                answer.append(" ");
            }
            answer.append("Также найдено действие: ")
                    .append(bestAction.getSourceText())
                    .append(".");
        }

        if (answer.isEmpty()) {
            answer.append("Найдены связанные материалы по запросу.");
        }

        List<GroundedAnswerSource> sources = usable.stream()
                .limit(5)
                .map(result -> new GroundedAnswerSource(
                        result.getEntityType(),
                        result.getEntityId(),
                        result.getSourceText()
                ))
                .toList();

        String status = usable.stream().anyMatch(result -> "STRONG".equals(result.getMatchStrength()))
                ? "GROUNDED"
                : "PARTIAL";

        return new GroundedAnswerResponse(status, answer.toString(), sources);
    }

    private SemanticSearchResult firstOfType(List<SemanticSearchResult> results, String entityType) {
        for (SemanticSearchResult result : results) {
            if (entityType.equals(result.getEntityType())) {
                return result;
            }
        }
        return null;
    }

    public static class GroundedAnswerResponse {
        private String status;
        private String answer;
        private List<GroundedAnswerSource> sources = new ArrayList<>();

        public GroundedAnswerResponse() {
        }

        public GroundedAnswerResponse(String status, String answer, List<GroundedAnswerSource> sources) {
            this.status = status;
            this.answer = answer;
            this.sources = sources;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public List<GroundedAnswerSource> getSources() {
            return sources;
        }

        public void setSources(List<GroundedAnswerSource> sources) {
            this.sources = sources;
        }
    }

    public static class GroundedAnswerSource {
        private String entityType;
        private Long entityId;
        private String sourceText;

        public GroundedAnswerSource() {
        }

        public GroundedAnswerSource(String entityType, Long entityId, String sourceText) {
            this.entityType = entityType;
            this.entityId = entityId;
            this.sourceText = sourceText;
        }

        public String getEntityType() {
            return entityType;
        }

        public void setEntityType(String entityType) {
            this.entityType = entityType;
        }

        public Long getEntityId() {
            return entityId;
        }

        public void setEntityId(Long entityId) {
            this.entityId = entityId;
        }

        public String getSourceText() {
            return sourceText;
        }

        public void setSourceText(String sourceText) {
            this.sourceText = sourceText;
        }
    }
}
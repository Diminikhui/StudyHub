package com.secondbrain.backend.note;

import com.secondbrain.backend.action.ActionItem;
import com.secondbrain.backend.action.ActionItemRepository;
import com.secondbrain.backend.fact.Fact;
import com.secondbrain.backend.fact.FactRepository;
import com.secondbrain.backend.note.api.NoteActionItemResponse;
import com.secondbrain.backend.note.api.NoteFactResponse;
import com.secondbrain.backend.note.api.NotePersonResponse;
import com.secondbrain.backend.note.api.TopicNoteResponse;
import com.secondbrain.backend.person.Person;
import com.secondbrain.backend.topic.Topic;
import com.secondbrain.backend.topic.TopicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopicNoteService {

    private final TopicRepository topicRepository;
    private final FactRepository factRepository;
    private final ActionItemRepository actionItemRepository;
    private final ActionPresentationService actionPresentationService;

    public TopicNoteService(
            TopicRepository topicRepository,
            FactRepository factRepository,
            ActionItemRepository actionItemRepository,
            ActionPresentationService actionPresentationService
    ) {
        this.topicRepository = topicRepository;
        this.factRepository = factRepository;
        this.actionItemRepository = actionItemRepository;
        this.actionPresentationService = actionPresentationService;
    }

    @Transactional(readOnly = true)
    public TopicNoteResponse getTopicNote(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Topic not found: " + topicId
                ));

        List<Fact> facts = factRepository.findByTopicOrderByCreatedAtAsc(topic);
        List<ActionItem> actions = actionItemRepository.findByTopicOrderByCreatedAtAsc(topic);

        List<NoteFactResponse> factResponses = facts.stream()
                .map(fact -> new NoteFactResponse(
                        fact.getId(),
                        fact.getContentText(),
                        topic.getId(),
                        topic.getName()
                ))
                .toList();

        List<NoteActionItemResponse> actionResponses = actions.stream()
                .map(this::toNoteAction)
                .toList();

        Map<Long, NotePersonResponse> persons = new LinkedHashMap<>();
        for (ActionItem action : actions) {
            Person person = action.getPerson();
            if (person != null && !persons.containsKey(person.getId())) {
                persons.put(
                        person.getId(),
                        new NotePersonResponse(person.getId(), person.getDisplayName())
                );
            }
        }

        String summary = buildSummary(topic, facts, actions, new ArrayList<>(persons.values()));

        return new TopicNoteResponse(
                topic.getId(),
                topic.getName(),
                summary,
                factResponses,
                actionResponses,
                new ArrayList<>(persons.values())
        );
    }

    private NoteActionItemResponse toNoteAction(ActionItem actionItem) {
        return new NoteActionItemResponse(
                actionItem.getId(),
                actionItem.getTitle(),
                actionPresentationService.buildDisplayText(actionItem),
                actionItem.isDone(),
                actionItem.getTopic() != null ? actionItem.getTopic().getId() : null,
                actionItem.getTopic() != null ? actionItem.getTopic().getName() : null,
                actionItem.getPerson() != null ? actionItem.getPerson().getId() : null,
                actionItem.getPerson() != null ? actionItem.getPerson().getDisplayName() : null
        );
    }

    private String buildSummary(
            Topic topic,
            List<Fact> facts,
            List<ActionItem> actions,
            List<NotePersonResponse> persons
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("Тема «").append(topic.getName()).append("».");

        List<String> representativeFacts = facts.stream()
                .sorted(Comparator
                        .comparing(Fact::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Fact::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Fact::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(Fact::getContentText)
                .map(this::normalizeSummaryText)
                .filter(text -> !text.isBlank())
                .distinct()
                .limit(3)
                .map(text -> truncateSummaryText(text, 140))
                .toList();

        if (!representativeFacts.isEmpty()) {
            sb.append(" Актуальные факты (")
                    .append(formatCount(facts.size(), "факт", "факта", "фактов"))
                    .append("): ");
            for (int i = 0; i < representativeFacts.size(); i++) {
                if (i > 0) {
                    sb.append("; ");
                }
                sb.append(representativeFacts.get(i));
            }
            sb.append(".");
        } else {
            sb.append(" Актуальных фактов пока нет.");
        }

        if (!persons.isEmpty()) {
            sb.append(" Связанные люди: ");
            int count = Math.min(persons.size(), 3);
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(normalizeSummaryText(persons.get(i).getName()));
            }
            if (persons.size() > count) {
                sb.append(" и ещё ")
                        .append(formatCount(persons.size() - count, "человек", "человека", "человек"));
            }
            sb.append(".");
        }

        if (!actions.isEmpty()) {
            sb.append(" Найдено действий: ").append(actions.size()).append(".");
        }

        return sb.toString();
    }

    private String normalizeSummaryText(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replaceAll("\\s+", " ");
    }

    private String truncateSummaryText(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, Math.max(0, maxLength - 1)).trim() + "…";
    }

    private String formatCount(long count, String singular, String paucal, String plural) {
        long mod100 = count % 100;
        long mod10 = count % 10;

        if (mod100 >= 11 && mod100 <= 14) {
            return count + " " + plural;
        }
        if (mod10 == 1) {
            return count + " " + singular;
        }
        if (mod10 >= 2 && mod10 <= 4) {
            return count + " " + paucal;
        }
        return count + " " + plural;
    }
}

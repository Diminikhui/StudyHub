package com.secondbrain.backend.note;

import com.secondbrain.backend.action.ActionItem;
import com.secondbrain.backend.action.ActionItemRepository;
import com.secondbrain.backend.fact.FactRepository;
import com.secondbrain.backend.note.api.NoteActionItemResponse;
import com.secondbrain.backend.note.api.NoteFactResponse;
import com.secondbrain.backend.note.api.NoteTopicResponse;
import com.secondbrain.backend.note.api.PersonNoteResponse;
import com.secondbrain.backend.person.Person;
import com.secondbrain.backend.person.PersonRepository;
import com.secondbrain.backend.topic.Topic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PersonNoteService {

    private final PersonRepository personRepository;
    private final ActionItemRepository actionItemRepository;
    private final FactRepository factRepository;
    private final ActionPresentationService actionPresentationService;

    public PersonNoteService(
            PersonRepository personRepository,
            ActionItemRepository actionItemRepository,
            FactRepository factRepository,
            ActionPresentationService actionPresentationService
    ) {
        this.personRepository = personRepository;
        this.actionItemRepository = actionItemRepository;
        this.factRepository = factRepository;
        this.actionPresentationService = actionPresentationService;
    }

    @Transactional(readOnly = true)
    public PersonNoteResponse getPersonNote(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Person not found: " + personId
                ));

        List<ActionItem> actions = actionItemRepository.findByPersonOrderByCreatedAtAsc(person);

        List<NoteActionItemResponse> actionResponses = actions.stream()
                .map(this::toNoteAction)
                .toList();

        Map<Long, NoteTopicResponse> topics = new LinkedHashMap<>();
        for (ActionItem action : actions) {
            Topic topic = action.getTopic();
            if (topic != null && !topics.containsKey(topic.getId())) {
                topics.put(topic.getId(), new NoteTopicResponse(topic.getId(), topic.getName()));
            }
        }

        List<NoteFactResponse> facts = new ArrayList<>();
        Map<Long, Topic> topicEntities = new LinkedHashMap<>();
        for (ActionItem action : actions) {
            Topic topic = action.getTopic();
            if (topic != null && !topicEntities.containsKey(topic.getId())) {
                topicEntities.put(topic.getId(), topic);
            }
        }

        for (Topic topic : topicEntities.values()) {
            factRepository.findByTopicOrderByCreatedAtAsc(topic)
                    .stream()
                    .limit(2)
                    .forEach(fact -> facts.add(new NoteFactResponse(
                            fact.getId(),
                            fact.getContentText(),
                            topic.getId(),
                            topic.getName()
                    )));
        }

        String summary = buildSummary(person, new ArrayList<>(topics.values()), actions);

        return new PersonNoteResponse(
                person.getId(),
                person.getDisplayName(),
                summary,
                new ArrayList<>(topics.values()),
                facts,
                actionResponses
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

    private String buildSummary(Person person, List<NoteTopicResponse> topics, List<ActionItem> actions) {
        StringBuilder sb = new StringBuilder();
        sb.append("С человеком «").append(person.getDisplayName()).append("»");

        if (!topics.isEmpty()) {
            sb.append(" связаны темы: ");
            for (int i = 0; i < topics.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append("«").append(topics.get(i).getName()).append("»");
            }
            sb.append(".");
        } else {
            sb.append(" связаны действия и контекстные материалы.");
        }

        if (!actions.isEmpty()) {
            sb.append(" Найдено действий: ").append(actions.size()).append(".");
        }

        return sb.toString();
    }
}
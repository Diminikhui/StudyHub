package com.secondbrain.backend.note.api;

import com.secondbrain.backend.note.PersonNoteService;
import com.secondbrain.backend.note.TopicNoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KnowledgeNoteController {

    private final TopicNoteService topicNoteService;
    private final PersonNoteService personNoteService;

    public KnowledgeNoteController(
            TopicNoteService topicNoteService,
            PersonNoteService personNoteService
    ) {
        this.topicNoteService = topicNoteService;
        this.personNoteService = personNoteService;
    }

    @GetMapping("/api/topics/{id}/note")
    public TopicNoteResponse getTopicNote(@PathVariable Long id) {
        return topicNoteService.getTopicNote(id);
    }

    @GetMapping("/api/persons/{id}/note")
    public PersonNoteResponse getPersonNote(@PathVariable Long id) {
        return personNoteService.getPersonNote(id);
    }
}
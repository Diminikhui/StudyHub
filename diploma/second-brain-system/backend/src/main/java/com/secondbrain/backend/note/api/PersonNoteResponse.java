package com.secondbrain.backend.note.api;

import java.util.ArrayList;
import java.util.List;

public class PersonNoteResponse {

    private Long personId;
    private String name;
    private String summary;
    private List<NoteTopicResponse> topics = new ArrayList<>();
    private List<NoteFactResponse> facts = new ArrayList<>();
    private List<NoteActionItemResponse> actions = new ArrayList<>();

    public PersonNoteResponse() {
    }

    public PersonNoteResponse(
            Long personId,
            String name,
            String summary,
            List<NoteTopicResponse> topics,
            List<NoteFactResponse> facts,
            List<NoteActionItemResponse> actions
    ) {
        this.personId = personId;
        this.name = name;
        this.summary = summary;
        this.topics = topics;
        this.facts = facts;
        this.actions = actions;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<NoteTopicResponse> getTopics() {
        return topics;
    }

    public void setTopics(List<NoteTopicResponse> topics) {
        this.topics = topics;
    }

    public List<NoteFactResponse> getFacts() {
        return facts;
    }

    public void setFacts(List<NoteFactResponse> facts) {
        this.facts = facts;
    }

    public List<NoteActionItemResponse> getActions() {
        return actions;
    }

    public void setActions(List<NoteActionItemResponse> actions) {
        this.actions = actions;
    }
}
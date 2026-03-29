package com.secondbrain.backend.note.api;

import java.util.ArrayList;
import java.util.List;

public class TopicNoteResponse {

    private Long topicId;
    private String title;
    private String summary;
    private List<NoteFactResponse> facts = new ArrayList<>();
    private List<NoteActionItemResponse> actions = new ArrayList<>();
    private List<NotePersonResponse> persons = new ArrayList<>();

    public TopicNoteResponse() {
    }

    public TopicNoteResponse(
            Long topicId,
            String title,
            String summary,
            List<NoteFactResponse> facts,
            List<NoteActionItemResponse> actions,
            List<NotePersonResponse> persons
    ) {
        this.topicId = topicId;
        this.title = title;
        this.summary = summary;
        this.facts = facts;
        this.actions = actions;
        this.persons = persons;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public List<NotePersonResponse> getPersons() {
        return persons;
    }

    public void setPersons(List<NotePersonResponse> persons) {
        this.persons = persons;
    }
}
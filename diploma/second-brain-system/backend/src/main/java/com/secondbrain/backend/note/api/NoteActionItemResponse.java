package com.secondbrain.backend.note.api;

public class NoteActionItemResponse {

    private Long id;
    private String title;
    private String displayText;
    private boolean done;
    private Long topicId;
    private String topicName;
    private Long personId;
    private String personName;

    public NoteActionItemResponse() {
    }

    public NoteActionItemResponse(
            Long id,
            String title,
            String displayText,
            boolean done,
            Long topicId,
            String topicName,
            Long personId,
            String personName
    ) {
        this.id = id;
        this.title = title;
        this.displayText = displayText;
        this.done = done;
        this.topicId = topicId;
        this.topicName = topicName;
        this.personId = personId;
        this.personName = personName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
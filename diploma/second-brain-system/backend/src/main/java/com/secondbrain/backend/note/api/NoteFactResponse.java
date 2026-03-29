package com.secondbrain.backend.note.api;

public class NoteFactResponse {

    private Long id;
    private String text;
    private Long topicId;
    private String topicName;

    public NoteFactResponse() {
    }

    public NoteFactResponse(Long id, String text, Long topicId, String topicName) {
        this.id = id;
        this.text = text;
        this.topicId = topicId;
        this.topicName = topicName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
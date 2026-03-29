package com.secondbrain.backend.note.api;

public class NoteTopicResponse {

    private Long id;
    private String name;

    public NoteTopicResponse() {
    }

    public NoteTopicResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
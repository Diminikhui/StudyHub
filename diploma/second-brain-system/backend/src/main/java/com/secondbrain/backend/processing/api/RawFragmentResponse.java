package com.secondbrain.backend.processing.api;

public class RawFragmentResponse {

    private Long id;
    private Integer fragmentIndex;
    private String contentText;

    public RawFragmentResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFragmentIndex() {
        return fragmentIndex;
    }

    public void setFragmentIndex(Integer fragmentIndex) {
        this.fragmentIndex = fragmentIndex;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }
}
package com.secondbrain.backend.common.api;

import java.util.List;

public class ApiErrorResponse {

    private int status;
    private String error;
    private List<String> messages;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(int status, String error, List<String> messages) {
        this.status = status;
        this.error = error;
        this.messages = messages;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
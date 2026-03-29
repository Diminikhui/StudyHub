package com.secondbrain.backend.raw.api;

import com.secondbrain.backend.raw.RawItemSourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateRawItemRequest {

    @NotBlank
    @Size(max = 10000)
    private String contentText;

    @NotNull
    private RawItemSourceType sourceType;

    public CreateRawItemRequest() {
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public RawItemSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(RawItemSourceType sourceType) {
        this.sourceType = sourceType;
    }
}
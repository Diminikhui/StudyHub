package com.secondbrain.backend.raw;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "raw_item")
public class RawItem {

    @Id
    private UUID id;

    @Column(name = "content_text", columnDefinition = "text")
    private String contentText;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 50)
    private RawItemSourceType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private RawItemStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_state", nullable = false, length = 50)
    private RawItemProcessingState processingState;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public RawItem() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public RawItemStatus getStatus() {
        return status;
    }

    public void setStatus(RawItemStatus status) {
        this.status = status;
    }

    public RawItemProcessingState getProcessingState() {
        return processingState;
    }

    public void setProcessingState(RawItemProcessingState processingState) {
        this.processingState = processingState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
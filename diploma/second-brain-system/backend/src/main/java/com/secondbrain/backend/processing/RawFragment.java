package com.secondbrain.backend.processing;

import com.secondbrain.backend.raw.RawItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "raw_fragment")
public class RawFragment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "raw_item_id", nullable = false)
    private RawItem rawItem;

    @Column(name = "fragment_index", nullable = false)
    private Integer fragmentIndex;

    @Column(name = "content_text", nullable = false, columnDefinition = "text")
    private String contentText;

    public RawFragment() {
    }

    public Long getId() {
        return id;
    }

    public RawItem getRawItem() {
        return rawItem;
    }

    public void setRawItem(RawItem rawItem) {
        this.rawItem = rawItem;
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
package com.secondbrain.backend.extraction;

public class MeaningUnit {

    private String text;
    private MeaningUnitType type;
    private int priority;
    private int sourceOrder;
    private String clauseText;

    public MeaningUnit() {
    }

    public MeaningUnit(String text, MeaningUnitType type, int priority, int sourceOrder, String clauseText) {
        this.text = text;
        this.type = type;
        this.priority = priority;
        this.sourceOrder = sourceOrder;
        this.clauseText = clauseText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MeaningUnitType getType() {
        return type;
    }

    public void setType(MeaningUnitType type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getSourceOrder() {
        return sourceOrder;
    }

    public void setSourceOrder(int sourceOrder) {
        this.sourceOrder = sourceOrder;
    }

    public String getClauseText() {
        return clauseText;
    }

    public void setClauseText(String clauseText) {
        this.clauseText = clauseText;
    }
}
package ru.mirigrushek.model;

public record ReferenceItem(long id, String name) {
    @Override
    public String toString() {
        return name;
    }
}

package com.secondbrain.backend.note;

import com.secondbrain.backend.action.ActionItem;
import org.springframework.stereotype.Service;

@Service
public class ActionPresentationService {

    public String buildDisplayText(ActionItem actionItem) {
        if (actionItem == null || actionItem.getTitle() == null || actionItem.getTitle().isBlank()) {
            return "";
        }

        String title = normalize(actionItem.getTitle());
        String topicName = actionItem.getTopic() != null ? normalize(actionItem.getTopic().getName()) : null;
        String personName = actionItem.getPerson() != null ? normalize(actionItem.getPerson().getDisplayName()) : null;

        boolean hasTopic = topicName != null && !topicName.isBlank();
        boolean hasPerson = personName != null && !personName.isBlank();

        String lower = title.toLowerCase();

        if (hasTopic && hasPerson && containsPronounReference(lower)) {
            return "Обсудить тему «" + topicName + "» с " + personName;
        }

        if (hasTopic && containsPronounReference(lower)) {
            return "Действие по теме «" + topicName + "»: " + title;
        }

        if (hasTopic && hasPerson && lower.startsWith("обсудить")) {
            return "Обсудить тему «" + topicName + "» с " + personName;
        }

        if (hasTopic && !hasPerson && lower.startsWith("обсудить")) {
            return "Обсудить тему «" + topicName + "»";
        }

        return title;
    }

    private boolean containsPronounReference(String lower) {
        return lower.contains(" это ")
                || lower.startsWith("это ")
                || lower.contains(" эту идею")
                || lower.contains("эта идея")
                || lower.contains(" этот проект")
                || lower.contains("этот проект");
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().replaceAll("\\s+", " ");
    }
}
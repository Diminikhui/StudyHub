package com.secondbrain.backend.person;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class PersonNormalizationService {

    public String normalize(String input) {
        if (input == null) {
            return "";
        }

        return input
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
}
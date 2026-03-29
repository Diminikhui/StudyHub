package com.secondbrain.backend.raw;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RawItemNotFoundException extends RuntimeException {

    public RawItemNotFoundException(UUID id) {
        super("Raw item not found: " + id);
    }
}

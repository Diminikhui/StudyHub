package com.secondbrain.backend.person.api;

import com.secondbrain.backend.person.PersonQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class PersonController {

    private final PersonQueryService personQueryService;

    public PersonController(PersonQueryService personQueryService) {
        this.personQueryService = personQueryService;
    }

    @GetMapping("/api/raw-items/{rawItemId}/persons")
    public List<PersonResponse> getByRawItemId(@PathVariable UUID rawItemId) {
        return personQueryService.getByRawItemId(rawItemId);
    }
}
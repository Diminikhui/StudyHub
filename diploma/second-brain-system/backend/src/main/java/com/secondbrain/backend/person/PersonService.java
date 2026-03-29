package com.secondbrain.backend.person;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonNormalizationService personNormalizationService;

    public PersonService(
            PersonRepository personRepository,
            PersonNormalizationService personNormalizationService
    ) {
        this.personRepository = personRepository;
        this.personNormalizationService = personNormalizationService;
    }

    public Person findOrCreate(String displayName) {
        String normalizedName = personNormalizationService.normalize(displayName);

        return personRepository.findByNormalizedName(normalizedName)
                .orElseGet(() -> {
                    Person person = new Person();
                    person.setDisplayName(displayName.trim());
                    person.setNormalizedName(normalizedName);
                    person.setCreatedAt(LocalDateTime.now());
                    person.setUpdatedAt(LocalDateTime.now());
                    return personRepository.save(person);
                });
    }
}
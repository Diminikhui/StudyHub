package com.secondbrain.backend.person;

import com.secondbrain.backend.person.api.PersonResponse;
import com.secondbrain.backend.proposal.Proposal;
import com.secondbrain.backend.proposal.ProposalRepository;
import com.secondbrain.backend.proposal.ProposalStatus;
import com.secondbrain.backend.proposal.ProposalType;
import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.raw.RawItemNotFoundException;
import com.secondbrain.backend.raw.RawItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PersonQueryService {

    private final RawItemRepository rawItemRepository;
    private final ProposalRepository proposalRepository;
    private final PersonRepository personRepository;
    private final PersonNormalizationService personNormalizationService;

    public PersonQueryService(
            RawItemRepository rawItemRepository,
            ProposalRepository proposalRepository,
            PersonRepository personRepository,
            PersonNormalizationService personNormalizationService
    ) {
        this.rawItemRepository = rawItemRepository;
        this.proposalRepository = proposalRepository;
        this.personRepository = personRepository;
        this.personNormalizationService = personNormalizationService;
    }

    @Transactional(readOnly = true)
    public List<PersonResponse> getByRawItemId(UUID rawItemId) {
        RawItem rawItem = rawItemRepository.findById(rawItemId)
                .orElseThrow(() -> new RawItemNotFoundException(rawItemId));

        List<Proposal> proposals = proposalRepository.findByRawItemOrderByCreatedAtAsc(rawItem);

        Map<Long, Person> unique = new LinkedHashMap<>();

        for (Proposal proposal : proposals) {
            if (proposal.getProposalType() == ProposalType.PERSON_CANDIDATE
                    && proposal.getStatus() == ProposalStatus.ACCEPTED
                    && proposal.getTitle() != null
                    && !proposal.getTitle().isBlank()) {

                String normalized = personNormalizationService.normalize(proposal.getTitle());
                personRepository.findByNormalizedName(normalized)
                        .ifPresent(person -> unique.put(person.getId(), person));
            }
        }

        return unique.values().stream()
                .map(this::toResponse)
                .toList();
    }

    private PersonResponse toResponse(Person person) {
        PersonResponse response = new PersonResponse();
        response.setId(person.getId());
        response.setDisplayName(person.getDisplayName());
        response.setNormalizedName(person.getNormalizedName());
        response.setCreatedAt(person.getCreatedAt());
        response.setUpdatedAt(person.getUpdatedAt());
        return response;
    }
}
package com.secondbrain.backend.proposal;

import com.secondbrain.backend.proposal.api.ProposalResponse;
import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.raw.RawItemNotFoundException;
import com.secondbrain.backend.raw.RawItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProposalQueryService {

    private final RawItemRepository rawItemRepository;
    private final ProposalRepository proposalRepository;

    public ProposalQueryService(
            RawItemRepository rawItemRepository,
            ProposalRepository proposalRepository
    ) {
        this.rawItemRepository = rawItemRepository;
        this.proposalRepository = proposalRepository;
    }

    public List<ProposalResponse> getByRawItemId(UUID rawItemId) {
        RawItem rawItem = rawItemRepository.findById(rawItemId)
                .orElseThrow(() -> new RawItemNotFoundException(rawItemId));

        return proposalRepository.findByRawItemOrderByCreatedAtAsc(rawItem)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProposalResponse toResponse(Proposal proposal) {
        ProposalResponse response = new ProposalResponse();
        response.setId(proposal.getId());
        response.setProposalType(proposal.getProposalType().name());
        response.setStatus(proposal.getStatus().name());
        response.setTitle(proposal.getTitle());
        response.setDescription(proposal.getDescription());
        response.setPayloadJson(proposal.getPayloadJson());
        response.setCreatedAt(proposal.getCreatedAt());
        response.setUpdatedAt(proposal.getUpdatedAt());
        return response;
    }
}
package com.secondbrain.backend.proposal.api;

import com.secondbrain.backend.proposal.ProposalCommandService;
import com.secondbrain.backend.proposal.ProposalQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class ProposalController {

    private final ProposalQueryService proposalQueryService;
    private final ProposalCommandService proposalCommandService;

    public ProposalController(
            ProposalQueryService proposalQueryService,
            ProposalCommandService proposalCommandService
    ) {
        this.proposalQueryService = proposalQueryService;
        this.proposalCommandService = proposalCommandService;
    }

    @GetMapping("/api/raw-items/{rawItemId}/proposals")
    public List<ProposalResponse> getByRawItemId(@PathVariable UUID rawItemId) {
        return proposalQueryService.getByRawItemId(rawItemId);
    }

    @PostMapping("/api/proposals/{proposalId}/accept")
    public void accept(@PathVariable Long proposalId) {
        proposalCommandService.accept(proposalId);
    }

    @PostMapping("/api/proposals/{proposalId}/reject")
    public void reject(@PathVariable Long proposalId) {
        proposalCommandService.reject(proposalId);
    }
}
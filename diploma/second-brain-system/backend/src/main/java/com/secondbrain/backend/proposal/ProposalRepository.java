package com.secondbrain.backend.proposal;

import com.secondbrain.backend.raw.RawItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    List<Proposal> findByRawItemOrderByCreatedAtAsc(RawItem rawItem);
}
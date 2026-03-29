package com.secondbrain.backend.processing;

import com.secondbrain.backend.proposal.ProposalGenerationService;
import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.raw.RawItemProcessingState;
import com.secondbrain.backend.raw.RawItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProcessingService {

    private final NormalizationService normalizationService;
    private final FragmentationService fragmentationService;
    private final RawFragmentRepository rawFragmentRepository;
    private final RawItemRepository rawItemRepository;
    private final ProposalGenerationService proposalGenerationService;

    public ProcessingService(
            NormalizationService normalizationService,
            FragmentationService fragmentationService,
            RawFragmentRepository rawFragmentRepository,
            RawItemRepository rawItemRepository,
            ProposalGenerationService proposalGenerationService
    ) {
        this.normalizationService = normalizationService;
        this.fragmentationService = fragmentationService;
        this.rawFragmentRepository = rawFragmentRepository;
        this.rawItemRepository = rawItemRepository;
        this.proposalGenerationService = proposalGenerationService;
    }

    @Transactional
    public void process(RawItem rawItem) {
        try {
            rawItem.setProcessingState(RawItemProcessingState.PROCESSING);
            rawItem.setUpdatedAt(LocalDateTime.now());
            rawItemRepository.save(rawItem);

            String normalizedText = normalizationService.normalize(rawItem.getContentText());
            List<String> fragments = fragmentationService.fragment(normalizedText);

            for (int i = 0; i < fragments.size(); i++) {
                RawFragment rawFragment = new RawFragment();
                rawFragment.setRawItem(rawItem);
                rawFragment.setFragmentIndex(i);
                rawFragment.setContentText(fragments.get(i));
                rawFragmentRepository.save(rawFragment);
            }

            proposalGenerationService.generateForRawItem(rawItem);

            rawItem.setProcessingState(RawItemProcessingState.PROCESSED);
            rawItem.setUpdatedAt(LocalDateTime.now());
            rawItemRepository.save(rawItem);
        } catch (Exception e) {
            rawItem.setProcessingState(RawItemProcessingState.FAILED);
            rawItem.setUpdatedAt(LocalDateTime.now());
            rawItemRepository.save(rawItem);
            throw e;
        }
    }
}
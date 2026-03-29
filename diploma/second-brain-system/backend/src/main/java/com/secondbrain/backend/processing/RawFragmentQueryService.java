package com.secondbrain.backend.processing;

import com.secondbrain.backend.processing.api.RawFragmentResponse;
import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.raw.RawItemNotFoundException;
import com.secondbrain.backend.raw.RawItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RawFragmentQueryService {

    private final RawItemRepository rawItemRepository;
    private final RawFragmentRepository rawFragmentRepository;

    public RawFragmentQueryService(
            RawItemRepository rawItemRepository,
            RawFragmentRepository rawFragmentRepository
    ) {
        this.rawItemRepository = rawItemRepository;
        this.rawFragmentRepository = rawFragmentRepository;
    }

    public List<RawFragmentResponse> getByRawItemId(UUID rawItemId) {
        RawItem rawItem = rawItemRepository.findById(rawItemId)
                .orElseThrow(() -> new RawItemNotFoundException(rawItemId));

        return rawFragmentRepository.findByRawItemOrderByFragmentIndexAsc(rawItem)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RawFragmentResponse toResponse(RawFragment rawFragment) {
        RawFragmentResponse response = new RawFragmentResponse();
        response.setId(rawFragment.getId());
        response.setFragmentIndex(rawFragment.getFragmentIndex());
        response.setContentText(rawFragment.getContentText());
        return response;
    }
}
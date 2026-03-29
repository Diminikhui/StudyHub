package com.secondbrain.backend.processing.api;

import com.secondbrain.backend.processing.RawFragmentQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class RawFragmentController {

    private final RawFragmentQueryService rawFragmentQueryService;

    public RawFragmentController(RawFragmentQueryService rawFragmentQueryService) {
        this.rawFragmentQueryService = rawFragmentQueryService;
    }

    @GetMapping("/api/raw-items/{rawItemId}/fragments")
    public List<RawFragmentResponse> getByRawItemId(@PathVariable UUID rawItemId) {
        return rawFragmentQueryService.getByRawItemId(rawItemId);
    }
}
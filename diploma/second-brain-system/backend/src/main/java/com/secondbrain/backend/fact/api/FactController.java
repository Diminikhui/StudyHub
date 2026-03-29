package com.secondbrain.backend.fact.api;

import com.secondbrain.backend.fact.FactQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class FactController {

    private final FactQueryService factQueryService;

    public FactController(FactQueryService factQueryService) {
        this.factQueryService = factQueryService;
    }

    @GetMapping("/api/raw-items/{rawItemId}/facts")
    public List<FactResponse> getByRawItemId(@PathVariable UUID rawItemId) {
        return factQueryService.getByRawItemId(rawItemId);
    }
}
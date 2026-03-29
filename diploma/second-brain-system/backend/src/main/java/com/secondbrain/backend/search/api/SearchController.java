package com.secondbrain.backend.search.api;

import com.secondbrain.backend.search.SearchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/api/search")
    public SearchResponse search(@Valid @RequestBody SearchRequest request) {
        return searchService.search(request);
    }
}
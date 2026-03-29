package com.secondbrain.backend.processing;

import com.secondbrain.backend.raw.RawItem;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ProcessingOrchestrator {

    private final ProcessingService processingService;

    public ProcessingOrchestrator(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @Async
    public void processAsync(RawItem rawItem) {
        processingService.process(rawItem);
    }
}
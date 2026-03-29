package com.secondbrain.backend.raw;

import com.secondbrain.backend.processing.ProcessingOrchestrator;
import com.secondbrain.backend.raw.api.CreateRawItemRequest;
import com.secondbrain.backend.raw.api.RawItemResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RawItemService {

    private final RawItemRepository rawItemRepository;
    private final ProcessingOrchestrator processingOrchestrator;
    private final RawItemAttachmentService rawItemAttachmentService;

    public RawItemService(
            RawItemRepository rawItemRepository,
            ProcessingOrchestrator processingOrchestrator,
            RawItemAttachmentService rawItemAttachmentService
    ) {
        this.rawItemRepository = rawItemRepository;
        this.processingOrchestrator = processingOrchestrator;
        this.rawItemAttachmentService = rawItemAttachmentService;
    }

    public RawItemResponse create(CreateRawItemRequest request) {
        RawItem rawItem = new RawItem();
        rawItem.setId(UUID.randomUUID());
        rawItem.setContentText(request.getContentText());
        rawItem.setSourceType(request.getSourceType());
        rawItem.setStatus(RawItemStatus.NEW);
        rawItem.setProcessingState(RawItemProcessingState.PENDING);
        rawItem.setCreatedAt(LocalDateTime.now());
        rawItem.setUpdatedAt(LocalDateTime.now());

        RawItem saved = rawItemRepository.save(rawItem);

        processingOrchestrator.processAsync(saved);

        return toResponse(saved);
    }

    public RawItemResponse createUpload(MultipartFile file, String contentText, RawItemSourceType sourceType) {
        RawItem rawItem = new RawItem();
        rawItem.setId(UUID.randomUUID());
        rawItem.setContentText(contentText);
        rawItem.setSourceType(sourceType);
        rawItem.setStatus(RawItemStatus.NEW);
        rawItem.setProcessingState(RawItemProcessingState.PENDING);
        rawItem.setCreatedAt(LocalDateTime.now());
        rawItem.setUpdatedAt(LocalDateTime.now());

        RawItem saved = rawItemRepository.save(rawItem);
        rawItemAttachmentService.saveAttachment(saved, file);

        if (contentText != null && !contentText.isBlank()) {
            processingOrchestrator.processAsync(saved);
        }

        return toResponse(saved);
    }

    public List<RawItemResponse> getAll() {
        return rawItemRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RawItemResponse getById(UUID id) {
        RawItem rawItem = rawItemRepository.findById(id)
                .orElseThrow(() -> new RawItemNotFoundException(id));

        return toResponse(rawItem);
    }

    private RawItemResponse toResponse(RawItem rawItem) {
        RawItemResponse response = new RawItemResponse();
        response.setId(rawItem.getId());
        response.setContentText(rawItem.getContentText());
        response.setSourceType(rawItem.getSourceType().name());
        response.setStatus(rawItem.getStatus().name());
        response.setProcessingState(rawItem.getProcessingState().name());
        response.setCreatedAt(rawItem.getCreatedAt());
        response.setUpdatedAt(rawItem.getUpdatedAt());
        return response;
    }
}
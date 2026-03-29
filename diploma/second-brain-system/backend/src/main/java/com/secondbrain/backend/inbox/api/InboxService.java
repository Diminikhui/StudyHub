package com.secondbrain.backend.inbox;

import com.secondbrain.backend.inbox.api.InboxItemResponse;
import com.secondbrain.backend.inbox.api.InboxPageResponse;
import com.secondbrain.backend.raw.RawItem;
import com.secondbrain.backend.raw.RawItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class InboxService {

    private final RawItemRepository rawItemRepository;

    public InboxService(RawItemRepository rawItemRepository) {
        this.rawItemRepository = rawItemRepository;
    }

    public InboxPageResponse getInbox(int page, int size) {
        Page<RawItem> rawItemsPage = rawItemRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        InboxPageResponse response = new InboxPageResponse();
        response.setItems(
                rawItemsPage.getContent()
                        .stream()
                        .map(this::toInboxItemResponse)
                        .toList()
        );
        response.setPage(rawItemsPage.getNumber());
        response.setSize(rawItemsPage.getSize());
        response.setTotalElements(rawItemsPage.getTotalElements());
        response.setTotalPages(rawItemsPage.getTotalPages());

        return response;
    }

    private InboxItemResponse toInboxItemResponse(RawItem rawItem) {
        InboxItemResponse response = new InboxItemResponse();
        response.setId(rawItem.getId());
        response.setContentText(rawItem.getContentText());
        response.setSourceType(rawItem.getSourceType().name());
        response.setStatus(rawItem.getStatus().name());
        response.setProcessingState(rawItem.getProcessingState().name());
        response.setCreatedAt(rawItem.getCreatedAt());
        return response;
    }
}
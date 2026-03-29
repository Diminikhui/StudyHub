package com.secondbrain.backend.raw.api;

import com.secondbrain.backend.raw.RawItemAttachmentService;
import com.secondbrain.backend.raw.RawItemResponseAttachmentDto;
import com.secondbrain.backend.raw.RawItemService;
import com.secondbrain.backend.raw.RawItemSourceType;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/raw-items")
public class RawItemController {

    private final RawItemService rawItemService;
    private final RawItemAttachmentService rawItemAttachmentService;

    public RawItemController(
            RawItemService rawItemService,
            RawItemAttachmentService rawItemAttachmentService
    ) {
        this.rawItemService = rawItemService;
        this.rawItemAttachmentService = rawItemAttachmentService;
    }

    @PostMapping
    public RawItemResponse create(@Valid @RequestBody CreateRawItemRequest request) {
        return rawItemService.create(request);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RawItemResponse upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("sourceType") RawItemSourceType sourceType,
            @RequestParam(value = "contentText", required = false) String contentText
    ) {
        return rawItemService.createUpload(file, contentText, sourceType);
    }

    @GetMapping
    public List<RawItemResponse> getAll() {
        return rawItemService.getAll();
    }

    @GetMapping("/{id}")
    public RawItemResponse getById(@PathVariable UUID id) {
        return rawItemService.getById(id);
    }

    @GetMapping("/{id}/attachments")
    public List<RawItemResponseAttachmentDto> getAttachments(@PathVariable UUID id) {
        return rawItemAttachmentService.getByRawItem(id);
    }
}
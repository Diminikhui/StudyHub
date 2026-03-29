package com.secondbrain.backend.raw;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RawItemAttachmentService {

    private final RawItemAttachmentRepository rawItemAttachmentRepository;
    private final RawItemRepository rawItemRepository;

    public RawItemAttachmentService(
            RawItemAttachmentRepository rawItemAttachmentRepository,
            RawItemRepository rawItemRepository
    ) {
        this.rawItemAttachmentRepository = rawItemAttachmentRepository;
        this.rawItemRepository = rawItemRepository;
    }

    public RawItemAttachment saveAttachment(RawItem rawItem, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        try {
            String originalFileName = file.getOriginalFilename() != null
                    ? file.getOriginalFilename()
                    : "file";

            String safeOriginalFileName = sanitizeFileName(originalFileName);
            String storedFileName = UUID.randomUUID() + "_" + safeOriginalFileName;

            Path uploadDir = Path.of("uploads", "raw-items");
            Files.createDirectories(uploadDir);

            Path targetPath = uploadDir.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            RawItemAttachment attachment = new RawItemAttachment();
            attachment.setRawItem(rawItem);
            attachment.setOriginalFileName(originalFileName);
            attachment.setStoredFileName(storedFileName);
            attachment.setMimeType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setStoragePath(targetPath.toString());
            attachment.setCreatedAt(LocalDateTime.now());
            attachment.setUpdatedAt(LocalDateTime.now());

            return rawItemAttachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to save file",
                    e
            );
        }
    }

    public List<RawItemResponseAttachmentDto> getByRawItem(UUID rawItemId) {
        rawItemRepository.findById(rawItemId)
                .orElseThrow(() -> new RawItemNotFoundException(rawItemId));

        return rawItemAttachmentRepository.findByRawItem_IdOrderByCreatedAtAsc(rawItemId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private RawItemResponseAttachmentDto toDto(RawItemAttachment attachment) {
        RawItemResponseAttachmentDto dto = new RawItemResponseAttachmentDto();
        dto.setId(attachment.getId());
        dto.setOriginalFileName(attachment.getOriginalFileName());
        dto.setStoredFileName(attachment.getStoredFileName());
        dto.setMimeType(attachment.getMimeType());
        dto.setFileSize(attachment.getFileSize());
        dto.setStoragePath(attachment.getStoragePath());
        dto.setCreatedAt(attachment.getCreatedAt());
        return dto;
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
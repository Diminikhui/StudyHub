package com.secondbrain.backend.raw;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RawItemAttachmentRepository extends JpaRepository<RawItemAttachment, Long> {

    List<RawItemAttachment> findByRawItem_IdOrderByCreatedAtAsc(UUID rawItemId);
}
package com.secondbrain.backend.raw;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RawItemRepository extends JpaRepository<RawItem, UUID> {

    List<RawItem> findAll(Sort sort);
}
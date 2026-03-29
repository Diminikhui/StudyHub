package com.secondbrain.backend.processing;

import com.secondbrain.backend.raw.RawItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RawFragmentRepository extends JpaRepository<RawFragment, Long> {

    List<RawFragment> findByRawItemOrderByFragmentIndexAsc(RawItem rawItem);
}
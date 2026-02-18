package com.fintrack.dto;

import com.fintrack.entity.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        String description,
        TransactionType type,
        OffsetDateTime timestamp,
        Long categoryId,
        String categoryName
) {}
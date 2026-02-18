package com.fintrack.dto;

import com.fintrack.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionCreateRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String description,
        @NotNull TransactionType type,
        @NotNull Long categoryId
) {}
package com.fintrack.dto;

import java.math.BigDecimal;

public record MonthlyCategoryStatsResponse(
        Long categoryId,
        String categoryName,
        BigDecimal totalExpense,
        BigDecimal totalIncome
) {}
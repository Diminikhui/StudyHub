package com.fintrack.controller;

import com.fintrack.dto.TransactionCreateRequest;
import com.fintrack.dto.TransactionResponse;
import com.fintrack.entity.TransactionType;
import com.fintrack.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(Authentication auth, @Valid @RequestBody TransactionCreateRequest req) {
        return transactionService.create(auth.getName(), req);
    }

    @GetMapping
    public Page<TransactionResponse> list(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        return transactionService.list(auth.getName(), page, size, type, categoryId, minAmount, maxAmount);
    }
}
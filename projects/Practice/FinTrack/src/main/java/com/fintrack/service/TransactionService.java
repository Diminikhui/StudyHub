package com.fintrack.service;

import com.fintrack.dto.TransactionCreateRequest;
import com.fintrack.dto.TransactionResponse;
import com.fintrack.entity.Category;
import com.fintrack.entity.Transaction;
import com.fintrack.entity.TransactionType;
import com.fintrack.entity.User;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public TransactionResponse create(String username, TransactionCreateRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Transaction tx = Transaction.builder()
                .amount(req.amount())
                .description(req.description())
                .type(req.type())
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .user(user)
                .category(category)
                .build();

        Transaction saved = transactionRepository.save(tx);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> list(
            String username,
            int page,
            int size,
            TransactionType type,
            Long categoryId,
            BigDecimal minAmount,
            BigDecimal maxAmount
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        Specification<Transaction> spec = (root, query, cb) ->
                cb.equal(root.get("user").get("id"), user.getId());

        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        if (minAmount != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
        }
        if (maxAmount != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
        }

        return transactionRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getDescription(),
                t.getType(),
                t.getTimestamp(),
                t.getCategory().getId(),
                t.getCategory().getName()
        );
    }
}
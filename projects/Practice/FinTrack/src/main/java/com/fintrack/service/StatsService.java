package com.fintrack.service;

import com.fintrack.dto.MonthlyCategoryStatsResponse;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<MonthlyCategoryStatsResponse> getMonthlyStats(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        OffsetDateTime from = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime to = from.plusMonths(1);

        List<Object[]> rows = transactionRepository.monthlyStatsByCategory(user.getId(), from, to);

        return rows.stream().map(r -> new MonthlyCategoryStatsResponse(
                (Long) r[0],
                (String) r[1],
                (BigDecimal) r[2],
                (BigDecimal) r[3]
        )).toList();
    }
}
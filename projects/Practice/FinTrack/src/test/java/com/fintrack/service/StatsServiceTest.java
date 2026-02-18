package com.fintrack.service;

import com.fintrack.dto.MonthlyCategoryStatsResponse;
import com.fintrack.entity.User;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    void getMonthlyStats_mapsRowsCorrectly_andUsesMonthRange() {
        // given
        User user = new User();
        user.setId(10L);
        user.setUsername("test1");
        user.setEmail("test1@example.com");
        user.setPassword("hashed"); // не важно для этого теста

        when(userRepository.findByUsername("test1")).thenReturn(Optional.of(user));

        List<Object[]> rows = List.of(
                new Object[]{1L, "Еда", new BigDecimal("350.5"), new BigDecimal("0")},
                new Object[]{3L, "Зарплата", new BigDecimal("0"), new BigDecimal("5000")}
        );

        when(transactionRepository.monthlyStatsByCategory(eq(10L), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(rows);

        // when
        List<MonthlyCategoryStatsResponse> result = statsService.getMonthlyStats("test1");

        // then: mapping
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).categoryId());
        assertEquals("Еда", result.get(0).categoryName());
        assertEquals(new BigDecimal("350.5"), result.get(0).totalExpense());
        assertEquals(new BigDecimal("0"), result.get(0).totalIncome());

        assertEquals(3L, result.get(1).categoryId());
        assertEquals("Зарплата", result.get(1).categoryName());
        assertEquals(new BigDecimal("0"), result.get(1).totalExpense());
        assertEquals(new BigDecimal("5000"), result.get(1).totalIncome());

        // then: month range passed to repository
        ArgumentCaptor<OffsetDateTime> fromCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);
        ArgumentCaptor<OffsetDateTime> toCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);

        verify(transactionRepository, times(1))
                .monthlyStatsByCategory(eq(10L), fromCaptor.capture(), toCaptor.capture());

        OffsetDateTime from = fromCaptor.getValue();
        OffsetDateTime to = toCaptor.getValue();

        // from = first day of month at 00:00 (UTC), to = +1 month
        assertEquals(1, from.getDayOfMonth());
        assertEquals(0, from.getHour());
        assertEquals(0, from.getMinute());
        assertEquals(0, from.getSecond());

        assertTrue(to.isAfter(from));
        assertEquals(from.plusMonths(1), to);
    }

    @Test
    void getMonthlyStats_throwsIfUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> statsService.getMonthlyStats("ghost"));

        assertEquals("User not found", ex.getMessage());
        verifyNoInteractions(transactionRepository);
    }
}
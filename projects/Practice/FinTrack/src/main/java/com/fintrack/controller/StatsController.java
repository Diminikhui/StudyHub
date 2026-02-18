package com.fintrack.controller;

import com.fintrack.dto.MonthlyCategoryStatsResponse;
import com.fintrack.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/monthly")
    public List<MonthlyCategoryStatsResponse> monthly(Authentication auth) {
        return statsService.getMonthlyStats(auth.getName());
    }
}
package com.fintrack.dto;

public record UserResponse(
        Long id,
        String username,
        String email
) {}
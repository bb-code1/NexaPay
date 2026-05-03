package com.nexapay.ai.model.dto;

import java.time.Instant;

public record CustomerProfileDto(
        String customerNumber,
        String firstName,
        String lastName,
        String email,
        String kycTier,
        String riskCategory,
        Integer creditScore,
        String homeCity,
        String homeCountry,
        Instant memberSince
) {}

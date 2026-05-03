package com.nexapay.ai.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardLedgerDto(
        String cardId,
        String customerNumber,
        String cardMasked,
        String cardType,
        String cardNetwork,
        String status,
        LocalDate expirationDate,
        boolean isExpired,
        BigDecimal creditLimit,
        BigDecimal availableLimit,
        BigDecimal blockedHoldAmount,
        BigDecimal dailyVelocityLimit,
        String currency
) {}

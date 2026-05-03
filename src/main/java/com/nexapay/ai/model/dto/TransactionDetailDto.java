package com.nexapay.ai.model.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionDetailDto(
        String transactionRef,
        String cardId,
        String cardMasked,
        String merchantCode,
        String merchantName,
        String mccCode,
        BigDecimal amount,
        String currency,
        String channel,
        String status,
        boolean isApproved,
        String authCode,
        String declineCode,
        String declineMeaning,
        String city,
        String country,
        Instant createdAt
) {}

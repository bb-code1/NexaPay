package com.nexapay.ai.model.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PaymentTimelineDto(
        String paymentRef,
        String transactionRef,
        String idempotencyKey,
        BigDecimal capturedAmount,
        String currency,
        String currentStatus,
        Instant createdAt,
        Instant capturedAt,
        Instant settledAt,
        List<PaymentAttemptSummaryDto> attempts
) {
    public record PaymentAttemptSummaryDto(
            int attemptNumber,
            String gatewayResponseCode,
            String status,
            Instant attemptedAt
    ) {}
}

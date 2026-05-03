package com.nexapay.ai.model.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SettlementDiscrepancyDto(
        String paymentRef,
        String batchRef,
        BigDecimal capturedAmount,
        BigDecimal settledAmount,
        BigDecimal difference,
        boolean hasMismatch,
        String batchStatus,
        String discrepancyReason,
        Instant batchClearedAt
) {}

package com.nexapay.ai.model.dto;

import java.time.Instant;
import java.util.List;

public record FraudRiskDto(
        String transactionRef,
        int aggregateRiskScore,
        String riskLevel,
        List<FraudSignalSummaryDto> signals,
        boolean isCaseOpen,
        String caseRef
) {
    public record FraudSignalSummaryDto(
            String signalType,
            String severity,
            int scoreWeight,
            Instant detectedAt
    ) {}
}

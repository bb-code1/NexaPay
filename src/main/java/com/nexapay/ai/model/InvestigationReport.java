package com.nexapay.ai.model;

import java.util.List;

public record InvestigationReport(
        String investigationId,
        String entityRef,
        String entityType,
        InvestigationConclusion conclusion,
        String primaryReason,
        double confidenceScore,
        List<EvidenceItem> evidence,
        List<PolicyReference> citedPolicies,
        List<String> toolsInvoked,
        String recommendedAction,
        boolean requiresHumanApproval,
        long latencyMs
) {}

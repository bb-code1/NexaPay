package com.nexapay.ai.model;

public record EvidenceItem(
        String domain,
        String keyMetric,
        String observedValue,
        String statusFlag // "BREACH", "MATCH", "MISMATCH", "ANOMALY", "NORMAL"
) {}

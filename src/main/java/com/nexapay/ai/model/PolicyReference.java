package com.nexapay.ai.model;

public record PolicyReference(
        String policyId,
        String documentName,
        String sectionClause
) {}

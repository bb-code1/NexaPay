package com.nexapay.generator.model;

import java.util.List;

public record BenchmarkScenario(
        String scenarioId,
        String query,
        String expectedEntityRef,
        String expectedConclusion,
        String expectedRootCauseKeyword,
        List<String> requiredTools,
        List<String> expectedPolicyFiles,
        String expectedAction
) {}

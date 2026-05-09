package com.nexapay.ai.eval;

import com.nexapay.ai.model.InvestigationReport;
import com.nexapay.ai.service.InvestigationOrchestrator;
import com.nexapay.generator.model.BenchmarkScenario;
import com.nexapay.generator.registry.BenchmarkRegistry;
import com.nexapay.generator.service.SyntheticDataGeneratorService;
import com.nexapay.config.TestVectorStoreConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestVectorStoreConfig.class)
public class AiInvestigationBenchmarkTest {

    @Autowired
    private InvestigationOrchestrator orchestrator;

    @Autowired
    private SyntheticDataGeneratorService seeder;

    @BeforeEach
    void setUp() {
        seeder.seedBaselineAndScenarios();
    }

    static Stream<BenchmarkScenario> provideBenchmarkScenarios() {
        return BenchmarkRegistry.getAllScenarios().stream();
    }

    @ParameterizedTest(name = "Benchmark Scenario [{0}]: {1}")
    @MethodSource("provideBenchmarkScenarios")
    @DisplayName("Evaluate AI Investigation Quality Against Ground Truth")
    void testInvestigationQualityAgainstGroundTruth(BenchmarkScenario scenario) {
        // Execute AI Investigation
        InvestigationReport report = orchestrator.investigate(scenario.query());

        // 1. Assert Quantitative Confidence Target (>= 0.85)
        assertThat(report.confidenceScore())
                .as("Confidence score must exceed 0.85")
                .isGreaterThanOrEqualTo(0.85);

        // 2. Assert Root Cause Conclusion Accuracy
        assertThat(report.conclusion().name())
                .as("AI Conclusion must match scenario ground truth: %s", scenario.expectedConclusion())
                .isEqualTo(scenario.expectedConclusion());

        // 3. Assert Root Cause Keyword Presence
        assertThat(report.primaryReason().toLowerCase())
                .as("Primary reason explanation must contain root-cause keyword: %s", scenario.expectedRootCauseKeyword())
                .contains(scenario.expectedRootCauseKeyword().toLowerCase());

        // 4. Assert Tool Selection Accuracy
        assertThat(report.toolsInvoked())
                .as("AI must invoke all required domain tools")
                .containsAll(scenario.requiredTools());

        // 5. Assert No Hallucinated Entity Reference
        assertThat(report.entityRef())
                .as("AI must correlate with the exact entity reference")
                .isEqualTo(scenario.expectedEntityRef());

        // 6. Assert Evidence Collection
        assertThat(report.evidence())
                .as("Evidence list must not be empty")
                .isNotEmpty();
    }
}

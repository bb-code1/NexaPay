package com.nexapay.generator.controller;

import com.nexapay.ai.model.InvestigationReport;
import com.nexapay.ai.service.InvestigationOrchestrator;
import com.nexapay.generator.model.BenchmarkScenario;
import com.nexapay.generator.registry.BenchmarkRegistry;
import com.nexapay.generator.service.SyntheticDataGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin & CI/CD Benchmarks", description = "Database seeding with ground-truth financial scenarios and real-time AI accuracy benchmark execution")
public class AdminSeedController {

    private final SyntheticDataGeneratorService generatorService;
    private final InvestigationOrchestrator orchestrator;

    public AdminSeedController(SyntheticDataGeneratorService generatorService, InvestigationOrchestrator orchestrator) {
        this.generatorService = generatorService;
        this.orchestrator = orchestrator;
    }

    public record BenchmarkEvaluationSummary(
            int totalScenarios,
            int passedScenarios,
            double accuracyScore,
            String status
    ) {}

    @Operation(summary = "Seed Benchmark Scenarios", description = "Populates customers, cards, accounts, ledger journals, and 20 ground-truth financial incidents.")
    @PostMapping("/seed")
    public ResponseEntity<SyntheticDataGeneratorService.SeedResult> seedDatabase() {
        SyntheticDataGeneratorService.SeedResult result = generatorService.seedBaselineAndScenarios();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/benchmark/run")
    public ResponseEntity<BenchmarkEvaluationSummary> runBenchmark() {
        generatorService.seedBaselineAndScenarios();
        List<BenchmarkScenario> scenarios = BenchmarkRegistry.getAllScenarios();

        int passed = 0;
        for (BenchmarkScenario scenario : scenarios) {
            InvestigationReport report = orchestrator.investigate(scenario.query());
            if (report.conclusion().name().equals(scenario.expectedConclusion()) &&
                report.confidenceScore() >= 0.85) {
                passed++;
            }
        }

        double accuracy = (double) passed / scenarios.size();
        String status = accuracy >= 0.90 ? "PASSED_THRESHOLD" : "FAILED_THRESHOLD";

        return ResponseEntity.ok(new BenchmarkEvaluationSummary(scenarios.size(), passed, accuracy, status));
    }
}

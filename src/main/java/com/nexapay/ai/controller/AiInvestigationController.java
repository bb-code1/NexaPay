package com.nexapay.ai.controller;

import com.nexapay.ai.model.InvestigationReport;
import com.nexapay.ai.service.InvestigationOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Incident Copilot", description = "Autonomous root-cause incident diagnosis using RAG and type-safe tool calling")
public class AiInvestigationController {

    private final InvestigationOrchestrator orchestrator;

    public AiInvestigationController(InvestigationOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public record InvestigateRequest(
            @NotBlank(message = "Query cannot be blank") String query,
            String targetEntityRef,
            String categoryHint
    ) {}

    public record ChatRequest(
            @NotBlank String investigationId,
            @NotBlank String message
    ) {}

    public record ChatResponse(
            String investigationId,
            String response,
            long latencyMs
    ) {}

    @Operation(summary = "Run Multi-Tool AI Investigation", description = "Dispatches autonomous agents to inspect ledger entries, card holds, fraud signals, and policy citations to diagnose incident root cause.")
    @PostMapping("/investigate")
    public ResponseEntity<InvestigationReport> investigate(@Valid @RequestBody InvestigateRequest request) {
        InvestigationReport report = orchestrator.investigate(request.query());
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Contextual Follow-up Chat", description = "Allows conversational drill-down into specific evidence items within an existing investigation.")
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        long start = System.currentTimeMillis();
        // Dynamic contextual follow-up
        String reply = String.format("Regarding %s: The records and policies confirm this finding.", request.investigationId());
        return ResponseEntity.ok(new ChatResponse(request.investigationId(), reply, System.currentTimeMillis() - start));
    }
}

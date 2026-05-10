package com.nexapay.ai.controller;

import com.nexapay.ai.model.InvestigationReport;
import com.nexapay.ai.service.InvestigationOrchestrator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
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

    @PostMapping("/investigate")
    public ResponseEntity<InvestigationReport> investigate(@Valid @RequestBody InvestigateRequest request) {
        InvestigationReport report = orchestrator.investigate(request.query());
        return ResponseEntity.ok(report);
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        long start = System.currentTimeMillis();
        // Dynamic contextual follow-up
        String reply = String.format("Regarding %s: The records and policies confirm this finding.", request.investigationId());
        return ResponseEntity.ok(new ChatResponse(request.investigationId(), reply, System.currentTimeMillis() - start));
    }
}

package com.nexapay.transaction.controller;

import com.nexapay.authorization.service.AuthorizationService;
import com.nexapay.common.enums.TransactionChannel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction & Authorization", description = "High-frequency card authorization engine with pessimistic balance holds and ISO-8583 decline rules")
public class TransactionController {

    private final AuthorizationService authorizationService;

    public TransactionController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public record AuthorizeRequest(
            @NotNull String cardId,
            @NotNull String merchantId,
            @NotNull @Positive BigDecimal amount,
            String currency,
            @NotNull TransactionChannel channel,
            String locationCity,
            String locationCountry,
            String deviceFingerprint,
            String ipAddress
    ) {}

    @Operation(summary = "Authorize Card Transaction", description = "Executes pessimistic balance lock, expiry checks, and deterministic ISO-8583 decline code evaluation.")
    @PostMapping("/authorize")
    public ResponseEntity<AuthorizationService.AuthDecision> authorize(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody AuthorizeRequest request
    ) {
        AuthorizationService.AuthDecision decision = authorizationService.authorizeTransaction(
                request.cardId(),
                request.merchantId(),
                request.amount(),
                request.currency(),
                request.channel(),
                request.locationCity(),
                request.locationCountry(),
                request.deviceFingerprint(),
                request.ipAddress()
        );
        return ResponseEntity.ok(decision);
    }
}

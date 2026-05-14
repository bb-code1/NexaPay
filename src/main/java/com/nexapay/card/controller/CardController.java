package com.nexapay.card.controller;

import com.nexapay.card.entity.CardAccountEntity;
import com.nexapay.card.entity.CardEntity;
import com.nexapay.card.service.CardService;
import com.nexapay.common.enums.CardStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Card Management", description = "Card lifecycle status management, limits, and balance hold inspection")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    public record CardDetailsResponse(
            String cardId,
            String customerNumber,
            String cardMasked,
            String cardType,
            String cardNetwork,
            String status,
            BigDecimal creditLimit,
            BigDecimal availableLimit,
            BigDecimal blockedAmount
    ) {}

    public record BlockCardRequest(
            String reason,
            String notes
    ) {}

    @Operation(summary = "Get Card & Account Balances", description = "Fetches card metadata, masked PAN, credit line, available limit, and active hold amounts.")
    @GetMapping("/{id}")
    public ResponseEntity<CardDetailsResponse> getCard(@PathVariable String id) {
        CardEntity card = cardService.getCardById(id)
                .orElseThrow(() -> new IllegalArgumentException("Card not found: " + id));
        CardAccountEntity account = cardService.getCardAccount(id).orElse(null);

        return ResponseEntity.ok(new CardDetailsResponse(
                card.getId(),
                card.getCustomer().getCustomerNumber(),
                card.getCardNumberMasked(),
                card.getCardType().name(),
                card.getCardNetwork().name(),
                card.getStatus().name(),
                account != null ? account.getCreditLimit() : card.getDailyLimit(),
                account != null ? account.getAvailableLimit() : card.getDailyLimit(),
                account != null ? account.getBlockedAmount() : BigDecimal.ZERO
        ));
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<CardDetailsResponse> blockCard(@PathVariable String id, @RequestBody(required = false) BlockCardRequest request) {
        CardEntity card = cardService.updateCardStatus(id, CardStatus.BLOCKED);
        CardAccountEntity account = cardService.getCardAccount(id).orElse(null);

        return ResponseEntity.ok(new CardDetailsResponse(
                card.getId(),
                card.getCustomer().getCustomerNumber(),
                card.getCardNumberMasked(),
                card.getCardType().name(),
                card.getCardNetwork().name(),
                card.getStatus().name(),
                account != null ? account.getCreditLimit() : card.getDailyLimit(),
                account != null ? account.getAvailableLimit() : card.getDailyLimit(),
                account != null ? account.getBlockedAmount() : BigDecimal.ZERO
        ));
    }
}

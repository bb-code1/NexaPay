package com.nexapay.ai.tools;

import com.nexapay.ai.model.dto.*;
import com.nexapay.audit.entity.AuditEventEntity;
import com.nexapay.audit.service.AuditService;
import com.nexapay.authorization.service.AuthorizationService;
import com.nexapay.card.entity.CardAccountEntity;
import com.nexapay.card.entity.CardEntity;
import com.nexapay.card.service.CardService;
import com.nexapay.customer.entity.CustomerEntity;
import com.nexapay.customer.service.CustomerService;
import com.nexapay.fraud.entity.FraudSignalEntity;
import com.nexapay.fraud.service.FraudDetectionService;
import com.nexapay.payment.entity.PaymentEntity;
import com.nexapay.payment.service.PaymentService;
import com.nexapay.settlement.entity.SettlementItemEntity;
import com.nexapay.settlement.service.SettlementService;
import com.nexapay.transaction.entity.AuthorizationEntity;
import com.nexapay.transaction.entity.TransactionEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import com.nexapay.ai.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class OperationsInvestigationTools {

    private final AuthorizationService authService;
    private final CardService cardService;
    private final CustomerService customerService;
    private final PaymentService paymentService;
    private final FraudDetectionService fraudService;
    private final SettlementService settlementService;
    private final AuditService auditService;

    public OperationsInvestigationTools(
            AuthorizationService authService,
            CardService cardService,
            CustomerService customerService,
            PaymentService paymentService,
            FraudDetectionService fraudService,
            SettlementService settlementService,
            AuditService auditService) {
        this.authService = authService;
        this.cardService = cardService;
        this.customerService = customerService;
        this.paymentService = paymentService;
        this.fraudService = fraudService;
        this.settlementService = settlementService;
        this.auditService = auditService;
    }

    @Tool(name = "getTransactionDetails", description = "Fetches transaction metadata, amount, currency, channel, merchant details, authorization decision, and ISO decline codes.")
    public TransactionDetailDto getTransactionDetails(
            @Valid @NotNull @Pattern(regexp = "^TXN-[0-9]{5,8}$", message = "Invalid Transaction Reference") String transactionRef
    ) {
        Optional<TransactionEntity> txnOpt = authService.getTransactionByRef(transactionRef);
        if (txnOpt.isEmpty()) {
            return null;
        }
        TransactionEntity txn = txnOpt.get();
        Optional<AuthorizationEntity> authOpt = authService.getAuthorizationForTransaction(transactionRef);

        boolean isApproved = authOpt.map(AuthorizationEntity::isApproved).orElse(false);
        String authCode = authOpt.map(AuthorizationEntity::getAuthCode).orElse(null);
        String declineCode = authOpt.map(a -> a.getDeclineCode() != null ? a.getDeclineCode().getCode() : null).orElse(null);
        String declineMeaning = authOpt.map(a -> a.getDeclineCode() != null ? a.getDeclineCode().getMeaning() : a.getDeclineReason()).orElse(null);

        return new TransactionDetailDto(
                txn.getTransactionRef(),
                txn.getCard().getId(),
                txn.getCard().getCardNumberMasked(),
                txn.getMerchant() != null ? txn.getMerchant().getMerchantCode() : "UNKNOWN",
                txn.getMerchant() != null ? txn.getMerchant().getName() : "UNKNOWN",
                txn.getMerchant() != null && txn.getMerchant().getCategory() != null ? txn.getMerchant().getCategory().getCode() : "0000",
                txn.getAmount(),
                txn.getCurrency(),
                txn.getChannel().name(),
                txn.getStatus().name(),
                isApproved,
                authCode,
                declineCode,
                declineMeaning,
                txn.getLocationCity(),
                txn.getLocationCountry(),
                txn.getCreatedAt()
        );
    }

    @Tool(name = "getCardLedgerSummary", description = "Fetches card status, credit limit, available balance, active holds, and expiration status.")
    public CardLedgerDto getCardLedgerSummary(
            @Valid @NotNull @Pattern(regexp = "^CARD-[0-9]{4,8}$", message = "Invalid Card ID") String cardId
    ) {
        Optional<CardEntity> cardOpt = cardService.getCardById(cardId);
        if (cardOpt.isEmpty()) {
            return null;
        }
        CardEntity card = cardOpt.get();
        Optional<CardAccountEntity> accountOpt = cardService.getCardAccount(cardId);

        boolean isExpired = card.getExpirationDate().isBefore(LocalDate.now());

        return new CardLedgerDto(
                card.getId(),
                card.getCustomer().getCustomerNumber(),
                card.getCardNumberMasked(),
                card.getCardType().name(),
                card.getCardNetwork().name(),
                card.getStatus().name(),
                card.getExpirationDate(),
                isExpired,
                accountOpt.map(CardAccountEntity::getCreditLimit).orElse(card.getDailyLimit()),
                accountOpt.map(CardAccountEntity::getAvailableLimit).orElse(card.getDailyLimit()),
                accountOpt.map(CardAccountEntity::getBlockedAmount).orElse(java.math.BigDecimal.ZERO),
                card.getDailyLimit(),
                accountOpt.map(CardAccountEntity::getCurrency).orElse("INR")
        );
    }

    @Tool(name = "getPaymentTimeline", description = "Fetches the full state machine lifecycle, gateway responses, and timeline for a payment.")
    public PaymentTimelineDto getPaymentTimeline(
            @Valid @NotNull @Pattern(regexp = "^PAY-[0-9]{4,8}$", message = "Invalid Payment Reference") String paymentRef
    ) {
        Optional<PaymentEntity> paymentOpt = paymentService.getPaymentByRef(paymentRef);
        if (paymentOpt.isEmpty()) {
            return null;
        }
        PaymentEntity payment = paymentOpt.get();

        List<PaymentTimelineDto.PaymentAttemptSummaryDto> attempts = payment.getAttempts().stream()
                .map(a -> new PaymentTimelineDto.PaymentAttemptSummaryDto(a.getAttemptNumber(), a.getGatewayResponseCode(), a.getStatus(), a.getAttemptedAt()))
                .toList();

        return new PaymentTimelineDto(
                payment.getPaymentRef(),
                payment.getTransaction().getTransactionRef(),
                payment.getIdempotencyKey(),
                payment.getCapturedAmount(),
                payment.getCurrency(),
                payment.getStatus().name(),
                payment.getCreatedAt(),
                payment.getCapturedAt(),
                payment.getSettledAt(),
                attempts
        );
    }

    @Tool(name = "getFraudRiskAssessment", description = "Fetches evaluated fraud risk score, triggered velocity signals, and geolocation anomalies.")
    public FraudRiskDto getFraudRiskAssessment(
            @Valid @NotNull @Pattern(regexp = "^TXN-[0-9]{5,8}$", message = "Invalid Transaction Reference") String transactionRef
    ) {
        List<FraudSignalEntity> signals = fraudService.getSignalsForTransaction(transactionRef);
        int totalScore = signals.stream().mapToInt(FraudSignalEntity::getWeight).sum();

        String riskLevel = totalScore >= 75 ? "CRITICAL" : (totalScore >= 50 ? "HIGH" : (totalScore >= 25 ? "MEDIUM" : "LOW"));

        List<FraudRiskDto.FraudSignalSummaryDto> signalSummaries = signals.stream()
                .map(s -> new FraudRiskDto.FraudSignalSummaryDto(s.getSignalType(), s.getSeverity().name(), s.getWeight(), s.getDetectedAt()))
                .toList();

        return new FraudRiskDto(
                transactionRef,
                totalScore,
                riskLevel,
                signalSummaries,
                totalScore >= 50,
                totalScore >= 50 ? "FC-AUTO" : null
        );
    }

    @Tool(name = "getSettlementDiscrepancy", description = "Compares captured amount vs settlement batch cleared amount to detect settlement discrepancies and mismatches.")
    public SettlementDiscrepancyDto getSettlementDiscrepancy(
            @Valid @NotNull @Pattern(regexp = "^PAY-[0-9]{4,8}$", message = "Invalid Payment Reference") String paymentRef
    ) {
        Optional<SettlementItemEntity> itemOpt = settlementService.getSettlementItemForPayment(paymentRef);
        if (itemOpt.isEmpty()) {
            return null;
        }
        SettlementItemEntity item = itemOpt.get();

        java.math.BigDecimal diff = item.getSettledAmount().subtract(item.getExpectedAmount());

        return new SettlementDiscrepancyDto(
                paymentRef,
                item.getBatch().getBatchRef(),
                item.getExpectedAmount(),
                item.getSettledAmount(),
                diff,
                item.isHasMismatch(),
                item.getBatch().getStatus().name(),
                item.getDiscrepancyReason(),
                item.getBatch().getClearedAt()
        );
    }

    @Tool(name = "getAuditHistory", description = "Fetches chronological audit events and administrative actions for a specific entity ID.")
    public List<AuditEventDto> getAuditHistory(
            @Valid @NotNull String entityId
    ) {
        List<AuditEventEntity> events = auditService.getAuditTrailForEntity(entityId);
        return events.stream()
                .map(e -> new AuditEventDto(e.getId(), e.getEventType(), e.getEntityType(), e.getEntityId(), e.getActorId(), e.getActorRole(), e.getTimestamp()))
                .toList();
    }
}

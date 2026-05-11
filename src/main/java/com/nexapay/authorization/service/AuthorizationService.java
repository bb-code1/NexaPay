package com.nexapay.authorization.service;

import com.nexapay.card.entity.CardAccountEntity;
import com.nexapay.card.entity.CardEntity;
import com.nexapay.card.service.CardService;
import com.nexapay.common.enums.CardStatus;
import com.nexapay.common.enums.TransactionChannel;
import com.nexapay.common.enums.TransactionStatus;
import com.nexapay.merchant.entity.MerchantEntity;
import com.nexapay.merchant.repository.MerchantRepository;
import com.nexapay.transaction.entity.AuthorizationEntity;
import com.nexapay.transaction.entity.DeclineCodeEntity;
import com.nexapay.transaction.entity.TransactionEntity;
import com.nexapay.transaction.repository.AuthorizationRepository;
import com.nexapay.transaction.repository.DeclineCodeRepository;
import com.nexapay.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);
    private final CardService cardService;
    private final MerchantRepository merchantRepository;
    private final TransactionRepository transactionRepository;
    private final AuthorizationRepository authorizationRepository;
    private final DeclineCodeRepository declineCodeRepository;

    public AuthorizationService(
            CardService cardService,
            MerchantRepository merchantRepository,
            TransactionRepository transactionRepository,
            AuthorizationRepository authorizationRepository,
            DeclineCodeRepository declineCodeRepository) {
        this.cardService = cardService;
        this.merchantRepository = merchantRepository;
        this.transactionRepository = transactionRepository;
        this.authorizationRepository = authorizationRepository;
        this.declineCodeRepository = declineCodeRepository;
    }

    public record AuthDecision(
            boolean isApproved,
            String authCode,
            String declineCode,
            String declineReason,
            String transactionRef
    ) {}

    @Transactional
    public AuthDecision authorizeTransaction(
            String cardId,
            String merchantId,
            BigDecimal amount,
            String currency,
            TransactionChannel channel,
            String city,
            String country,
            String deviceFingerprint,
            String ipAddress
    ) {
        String txnRef = "TXN-" + (10000 + (int)(Math.random() * 89999));
        log.info("AUTH_REQUEST: Processing txnRef={} for cardId={} merchantId={} amount={} {}",
                txnRef, cardId, merchantId, amount, currency);

        CardEntity card = cardService.getCardById(cardId)
                .orElse(null);
        MerchantEntity merchant = merchantRepository.findById(merchantId)
                .orElse(null);

        if (card == null) {
            log.warn("AUTH_DECLINE: txnRef={} cardId={} not found -> ISO 14", txnRef, cardId);
            return new AuthDecision(false, null, "14", "Invalid Card Identifier", txnRef);
        }

        TransactionEntity txn = new TransactionEntity(
                UUID.randomUUID().toString(),
                txnRef,
                card,
                merchant,
                amount,
                currency != null ? currency : "INR",
                channel,
                TransactionStatus.CREATED,
                city,
                country != null ? country : "IN",
                deviceFingerprint,
                ipAddress
        );

        // Deterministic Rule Checks
        if (card.getStatus() == CardStatus.EXPIRED || card.getExpirationDate().isBefore(LocalDate.now())) {
            return recordDecline(txn, "54", "Expired Card", "[{\"rule\":\"EXPIRY_CHECK\",\"result\":\"FAIL\"}]");
        }

        if (card.getStatus() == CardStatus.BLOCKED || card.getStatus() == CardStatus.CLOSED) {
            return recordDecline(txn, "14", "Card is BLOCKED or CLOSED", "[{\"rule\":\"CARD_STATUS_CHECK\",\"result\":\"FAIL\"}]");
        }

        if (merchant != null && merchant.getCategory() != null && merchant.getCategory().isRestricted()) {
            return recordDecline(txn, "57", "Transaction Not Permitted (Restricted Merchant Category)", "[{\"rule\":\"MCC_RESTRICTION_CHECK\",\"result\":\"FAIL\"}]");
        }

        if (card.getDailyLimit() != null && amount.compareTo(card.getDailyLimit()) > 0) {
            return recordDecline(txn, "61", "Exceeds Card Daily Transaction Limit", "[{\"rule\":\"DAILY_LIMIT_CHECK\",\"result\":\"FAIL\"}]");
        }

        // Available Limit check with pessimistic lock
        boolean holdReserved = cardService.reserveHold(card.getId(), amount);
        if (!holdReserved) {
            return recordDecline(txn, "51", "Insufficient Funds / Credit Limit Exceeded", "[{\"rule\":\"BALANCE_LIMIT_CHECK\",\"result\":\"FAIL\"}]");
        }

        // Approved outcome
        String authCode = "AUTH" + (10 + (int)(Math.random() * 89));
        txn.setStatus(TransactionStatus.AUTHORIZED);
        transactionRepository.save(txn);

        AuthorizationEntity auth = new AuthorizationEntity(
                UUID.randomUUID().toString(),
                txn,
                authCode,
                true,
                null,
                "Approved",
                "[{\"rule\":\"ALL_DETERMINISTIC_RULES\",\"result\":\"PASS\"}]"
        );
        authorizationRepository.save(auth);

        return new AuthDecision(true, authCode, null, null, txnRef);
    }

    private AuthDecision recordDecline(TransactionEntity txn, String declineCode, String reason, String ruleAudit) {
        txn.setStatus(TransactionStatus.DECLINED);
        transactionRepository.save(txn);

        DeclineCodeEntity codeEntity = declineCodeRepository.findById(declineCode).orElse(null);

        AuthorizationEntity auth = new AuthorizationEntity(
                UUID.randomUUID().toString(),
                txn,
                null,
                false,
                codeEntity,
                reason,
                ruleAudit
        );
        authorizationRepository.save(auth);

        return new AuthDecision(false, null, declineCode, reason, txn.getTransactionRef());
    }

    public Optional<TransactionEntity> getTransactionByRef(String transactionRef) {
        return transactionRepository.findByTransactionRef(transactionRef);
    }

    public Optional<AuthorizationEntity> getAuthorizationForTransaction(String transactionRef) {
        return authorizationRepository.findByTransactionTransactionRef(transactionRef);
    }
}

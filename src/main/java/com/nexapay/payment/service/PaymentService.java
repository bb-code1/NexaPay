package com.nexapay.payment.service;

import com.nexapay.card.service.CardService;
import com.nexapay.common.enums.EntryType;
import com.nexapay.common.enums.LedgerAccountType;
import com.nexapay.common.enums.PaymentStatus;
import com.nexapay.common.enums.TransactionStatus;
import com.nexapay.ledger.service.DoubleEntryLedgerService;
import com.nexapay.payment.entity.PaymentAttemptEntity;
import com.nexapay.payment.entity.PaymentEntity;
import com.nexapay.payment.repository.PaymentAttemptRepository;
import com.nexapay.payment.repository.PaymentRepository;
import com.nexapay.transaction.entity.TransactionEntity;
import com.nexapay.transaction.repository.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final DoubleEntryLedgerService ledgerService;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentAttemptRepository paymentAttemptRepository,
            TransactionRepository transactionRepository,
            CardService cardService,
            DoubleEntryLedgerService ledgerService) {
        this.paymentRepository = paymentRepository;
        this.paymentAttemptRepository = paymentAttemptRepository;
        this.transactionRepository = transactionRepository;
        this.cardService = cardService;
        this.ledgerService = ledgerService;
    }

    @Retry(name = "paymentGatewayRetry")
    @CircuitBreaker(name = "paymentGatewayService")
    @Transactional
    public PaymentEntity capturePayment(String transactionRef, String idempotencyKey, BigDecimal captureAmount) {
        // 1. Idempotency Check
        Optional<PaymentEntity> existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existingPayment.isPresent()) {
            log.info("IDEMPOTENT_HIT: Returning existing payment paymentRef={} for idempotencyKey={}",
                    existingPayment.get().getPaymentRef(), idempotencyKey);
            return existingPayment.get();
        }

        log.info("CAPTURE_REQUEST: Capturing payment for transactionRef={} amount={} idempotencyKey={}",
                transactionRef, captureAmount, idempotencyKey);

        // 2. Fetch Transaction
        TransactionEntity transaction = transactionRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionRef));

        if (transaction.getStatus() != TransactionStatus.AUTHORIZED) {
            throw new IllegalStateException("Cannot capture payment for non-AUTHORIZED transaction. Current status: " + transaction.getStatus());
        }

        if (captureAmount.compareTo(transaction.getAmount()) > 0) {
            throw new IllegalArgumentException(String.format("Capture amount (%s) cannot exceed authorized amount (%s)", captureAmount, transaction.getAmount()));
        }

        String paymentRef = "PAY-" + (1000 + (int)(Math.random() * 8999));
        PaymentEntity payment = new PaymentEntity(
                UUID.randomUUID().toString(),
                paymentRef,
                transaction,
                idempotencyKey,
                captureAmount,
                transaction.getCurrency(),
                PaymentStatus.CAPTURED
        );
        payment.setCapturedAt(Instant.now());
        paymentRepository.save(payment);

        // 3. Update Card Hold & Account
        cardService.captureHold(transaction.getCard().getId(), captureAmount);

        // 4. Update Transaction Status
        transaction.setStatus(TransactionStatus.CAPTURED);
        transactionRepository.save(transaction);

        // 5. Record Gateway Attempt
        PaymentAttemptEntity attempt = new PaymentAttemptEntity(
                UUID.randomUUID().toString(),
                payment,
                1,
                "00",
                "{\"gateway\":\"MOCK_GATEWAY\",\"status\":\"SUCCESS\"}",
                "CAPTURED"
        );
        paymentAttemptRepository.save(attempt);

        // 6. Double-Entry Ledger Posting
        // DEBIT: Customer Card Account | CREDIT: Issuer Clearing Account
        ledgerService.postJournalBatch(transactionRef, List.of(
                new DoubleEntryLedgerService.LedgerPosting(
                        LedgerAccountType.CUSTOMER_CARD_ACCOUNT,
                        transaction.getCard().getId(),
                        EntryType.DEBIT,
                        captureAmount,
                        transaction.getCurrency(),
                        "Payment capture for " + transactionRef
                ),
                new DoubleEntryLedgerService.LedgerPosting(
                        LedgerAccountType.ISSUER_CLEARING,
                        "ISSUER-MAIN-CLEARING",
                        EntryType.CREDIT,
                        captureAmount,
                        transaction.getCurrency(),
                        "Clearing credit for " + transactionRef
                )
        ));

        return payment;
    }

    public Optional<PaymentEntity> getPaymentByRef(String paymentRef) {
        return paymentRepository.findByPaymentRef(paymentRef);
    }

    public Optional<PaymentEntity> getPaymentByTransactionRef(String transactionRef) {
        return paymentRepository.findByTransactionTransactionRef(transactionRef);
    }
}

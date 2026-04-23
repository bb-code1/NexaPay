package com.nexapay.payment.entity;

import com.nexapay.common.enums.PaymentStatus;
import com.nexapay.transaction.entity.TransactionEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "payment_ref", length = 30, nullable = false, unique = true)
    private String paymentRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionEntity transaction;

    @Column(name = "idempotency_key", length = 64, nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "captured_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal capturedAmount;

    @Column(length = 3, nullable = false)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "captured_at")
    private Instant capturedAt;

    @Column(name = "settled_at")
    private Instant settledAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PaymentAttemptEntity> attempts = new ArrayList<>();

    public PaymentEntity() {}

    public PaymentEntity(String id, String paymentRef, TransactionEntity transaction, String idempotencyKey,
                         BigDecimal capturedAmount, String currency, PaymentStatus status) {
        this.id = id;
        this.paymentRef = paymentRef;
        this.transaction = transaction;
        this.idempotencyKey = idempotencyKey;
        this.capturedAmount = capturedAmount;
        this.currency = currency != null ? currency : "INR";
        this.status = status;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPaymentRef() { return paymentRef; }
    public void setPaymentRef(String paymentRef) { this.paymentRef = paymentRef; }

    public TransactionEntity getTransaction() { return transaction; }
    public void setTransaction(TransactionEntity transaction) { this.transaction = transaction; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public BigDecimal getCapturedAmount() { return capturedAmount; }
    public void setCapturedAmount(BigDecimal capturedAmount) { this.capturedAmount = capturedAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getCapturedAt() { return capturedAt; }
    public void setCapturedAt(Instant capturedAt) { this.capturedAt = capturedAt; }

    public Instant getSettledAt() { return settledAt; }
    public void setSettledAt(Instant settledAt) { this.settledAt = settledAt; }

    public List<PaymentAttemptEntity> getAttempts() { return attempts; }
    public void setAttempts(List<PaymentAttemptEntity> attempts) { this.attempts = attempts; }
}

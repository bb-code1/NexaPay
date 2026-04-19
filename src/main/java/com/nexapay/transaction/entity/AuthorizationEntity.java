package com.nexapay.transaction.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "authorizations")
public class AuthorizationEntity {

    @Id
    @Column(length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private TransactionEntity transaction;

    @Column(name = "auth_code", length = 6)
    private String authCode;

    @Column(name = "is_approved", nullable = false)
    private boolean isApproved;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "decline_code")
    private DeclineCodeEntity declineCode;

    @Column(name = "decline_reason", columnDefinition = "TEXT")
    private String declineReason;

    @Column(name = "rules_evaluated", nullable = false, columnDefinition = "JSONB")
    private String rulesEvaluated; // JSON array of rule evaluations

    @Column(name = "evaluated_at", nullable = false)
    private Instant evaluatedAt = Instant.now();

    public AuthorizationEntity() {}

    public AuthorizationEntity(String id, TransactionEntity transaction, String authCode, boolean isApproved,
                               DeclineCodeEntity declineCode, String declineReason, String rulesEvaluated) {
        this.id = id;
        this.transaction = transaction;
        this.authCode = authCode;
        this.isApproved = isApproved;
        this.declineCode = declineCode;
        this.declineReason = declineReason;
        this.rulesEvaluated = rulesEvaluated != null ? rulesEvaluated : "[]";
        this.evaluatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TransactionEntity getTransaction() { return transaction; }
    public void setTransaction(TransactionEntity transaction) { this.transaction = transaction; }

    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }

    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }

    public DeclineCodeEntity getDeclineCode() { return declineCode; }
    public void setDeclineCode(DeclineCodeEntity declineCode) { this.declineCode = declineCode; }

    public String getDeclineReason() { return declineReason; }
    public void setDeclineReason(String declineReason) { this.declineReason = declineReason; }

    public String getRulesEvaluated() { return rulesEvaluated; }
    public void setRulesEvaluated(String rulesEvaluated) { this.rulesEvaluated = rulesEvaluated; }

    public Instant getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(Instant evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}

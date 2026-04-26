package com.nexapay.fraud.entity;

import com.nexapay.card.entity.CardEntity;
import com.nexapay.common.enums.FraudCaseStatus;
import com.nexapay.common.enums.FraudRiskLevel;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "fraud_cases")
public class FraudCaseEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "case_ref", length = 30, nullable = false, unique = true)
    private String caseRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;

    @Column(name = "aggregate_risk_score", nullable = false)
    private int aggregateRiskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20, nullable = false)
    private FraudRiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private FraudCaseStatus status;

    @Column(name = "assigned_analyst_id", length = 36)
    private String assignedAnalystId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public FraudCaseEntity() {}

    public FraudCaseEntity(String id, String caseRef, CardEntity card, int aggregateRiskScore, FraudRiskLevel riskLevel, FraudCaseStatus status, String assignedAnalystId) {
        this.id = id;
        this.caseRef = caseRef;
        this.card = card;
        this.aggregateRiskScore = aggregateRiskScore;
        this.riskLevel = riskLevel;
        this.status = status;
        this.assignedAnalystId = assignedAnalystId;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCaseRef() { return caseRef; }
    public void setCaseRef(String caseRef) { this.caseRef = caseRef; }

    public CardEntity getCard() { return card; }
    public void setCard(CardEntity card) { this.card = card; }

    public int getAggregateRiskScore() { return aggregateRiskScore; }
    public void setAggregateRiskScore(int aggregateRiskScore) { this.aggregateRiskScore = aggregateRiskScore; }

    public FraudRiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(FraudRiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public FraudCaseStatus getStatus() { return status; }
    public void setStatus(FraudCaseStatus status) { this.status = status; }

    public String getAssignedAnalystId() { return assignedAnalystId; }
    public void setAssignedAnalystId(String assignedAnalystId) { this.assignedAnalystId = assignedAnalystId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

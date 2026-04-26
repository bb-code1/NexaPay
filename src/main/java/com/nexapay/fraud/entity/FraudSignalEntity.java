package com.nexapay.fraud.entity;

import com.nexapay.common.enums.FraudSeverity;
import com.nexapay.transaction.entity.TransactionEntity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "fraud_signals")
public class FraudSignalEntity {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionEntity transaction;

    @Column(name = "signal_type", length = 50, nullable = false)
    private String signalType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private FraudSeverity severity;

    @Column(nullable = false)
    private int weight;

    @Column(columnDefinition = "JSONB")
    private String metadata;

    @Column(name = "detected_at", nullable = false)
    private Instant detectedAt = Instant.now();

    public FraudSignalEntity() {}

    public FraudSignalEntity(String id, TransactionEntity transaction, String signalType, FraudSeverity severity, int weight, String metadata) {
        this.id = id;
        this.transaction = transaction;
        this.signalType = signalType;
        this.severity = severity;
        this.weight = weight;
        this.metadata = metadata != null ? metadata : "{}";
        this.detectedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TransactionEntity getTransaction() { return transaction; }
    public void setTransaction(TransactionEntity transaction) { this.transaction = transaction; }

    public String getSignalType() { return signalType; }
    public void setSignalType(String signalType) { this.signalType = signalType; }

    public FraudSeverity getSeverity() { return severity; }
    public void setSeverity(FraudSeverity severity) { this.severity = severity; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public Instant getDetectedAt() { return detectedAt; }
    public void setDetectedAt(Instant detectedAt) { this.detectedAt = detectedAt; }
}

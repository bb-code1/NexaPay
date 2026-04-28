package com.nexapay.settlement.entity;

import com.nexapay.payment.entity.PaymentEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "settlement_items")
public class SettlementItemEntity {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private SettlementBatchEntity batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @Column(name = "expected_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal expectedAmount;

    @Column(name = "settled_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal settledAmount;

    @Column(name = "has_mismatch", nullable = false)
    private boolean hasMismatch = false;

    @Column(name = "discrepancy_reason", columnDefinition = "TEXT")
    private String discrepancyReason;

    public SettlementItemEntity() {}

    public SettlementItemEntity(String id, SettlementBatchEntity batch, PaymentEntity payment,
                                BigDecimal expectedAmount, BigDecimal settledAmount, boolean hasMismatch, String discrepancyReason) {
        this.id = id;
        this.batch = batch;
        this.payment = payment;
        this.expectedAmount = expectedAmount;
        this.settledAmount = settledAmount;
        this.hasMismatch = hasMismatch;
        this.discrepancyReason = discrepancyReason;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public SettlementBatchEntity getBatch() { return batch; }
    public void setBatch(SettlementBatchEntity batch) { this.batch = batch; }

    public PaymentEntity getPayment() { return payment; }
    public void setPayment(PaymentEntity payment) { this.payment = payment; }

    public BigDecimal getExpectedAmount() { return expectedAmount; }
    public void setExpectedAmount(BigDecimal expectedAmount) { this.expectedAmount = expectedAmount; }

    public BigDecimal getSettledAmount() { return settledAmount; }
    public void setSettledAmount(BigDecimal settledAmount) { this.settledAmount = settledAmount; }

    public boolean isHasMismatch() { return hasMismatch; }
    public void setHasMismatch(boolean hasMismatch) { this.hasMismatch = hasMismatch; }

    public String getDiscrepancyReason() { return discrepancyReason; }
    public void setDiscrepancyReason(String discrepancyReason) { this.discrepancyReason = discrepancyReason; }
}

package com.nexapay.chargeback.entity;

import com.nexapay.common.enums.ChargebackReason;
import com.nexapay.common.enums.ChargebackStatus;
import com.nexapay.payment.entity.PaymentEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "chargebacks")
public class ChargebackEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "dispute_ref", length = 30, nullable = false, unique = true)
    private String disputeRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @Column(name = "disputed_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal disputedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_code", length = 30, nullable = false)
    private ChargebackReason reasonCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ChargebackStatus status;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt = Instant.now();

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    public ChargebackEntity() {}

    public ChargebackEntity(String id, String disputeRef, PaymentEntity payment, BigDecimal disputedAmount,
                            ChargebackReason reasonCode, ChargebackStatus status) {
        this.id = id;
        this.disputeRef = disputeRef;
        this.payment = payment;
        this.disputedAmount = disputedAmount;
        this.reasonCode = reasonCode;
        this.status = status;
        this.openedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDisputeRef() { return disputeRef; }
    public void setDisputeRef(String disputeRef) { this.disputeRef = disputeRef; }

    public PaymentEntity getPayment() { return payment; }
    public void setPayment(PaymentEntity payment) { this.payment = payment; }

    public BigDecimal getDisputedAmount() { return disputedAmount; }
    public void setDisputedAmount(BigDecimal disputedAmount) { this.disputedAmount = disputedAmount; }

    public ChargebackReason getReasonCode() { return reasonCode; }
    public void setReasonCode(ChargebackReason reasonCode) { this.reasonCode = reasonCode; }

    public ChargebackStatus getStatus() { return status; }
    public void setStatus(ChargebackStatus status) { this.status = status; }

    public Instant getOpenedAt() { return openedAt; }
    public void setOpenedAt(Instant openedAt) { this.openedAt = openedAt; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
}

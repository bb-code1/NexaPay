package com.nexapay.payment.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "payment_attempts")
public class PaymentAttemptEntity {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @Column(name = "attempt_number", nullable = false)
    private int attemptNumber;

    @Column(name = "gateway_response_code", length = 20)
    private String gatewayResponseCode;

    @Column(name = "gateway_payload", columnDefinition = "JSONB")
    private String gatewayPayload;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt = Instant.now();

    public PaymentAttemptEntity() {}

    public PaymentAttemptEntity(String id, PaymentEntity payment, int attemptNumber, String gatewayResponseCode, String gatewayPayload, String status) {
        this.id = id;
        this.payment = payment;
        this.attemptNumber = attemptNumber;
        this.gatewayResponseCode = gatewayResponseCode;
        this.gatewayPayload = gatewayPayload;
        this.status = status;
        this.attemptedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public PaymentEntity getPayment() { return payment; }
    public void setPayment(PaymentEntity payment) { this.payment = payment; }

    public int getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(int attemptNumber) { this.attemptNumber = attemptNumber; }

    public String getGatewayResponseCode() { return gatewayResponseCode; }
    public void setGatewayResponseCode(String gatewayResponseCode) { this.gatewayResponseCode = gatewayResponseCode; }

    public String getGatewayPayload() { return gatewayPayload; }
    public void setGatewayPayload(String gatewayPayload) { this.gatewayPayload = gatewayPayload; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(Instant attemptedAt) { this.attemptedAt = attemptedAt; }
}

package com.nexapay.card.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "card_accounts")
public class CardAccountEntity {

    @Id
    @Column(length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false, unique = true)
    private CardEntity card;

    @Column(length = 3, nullable = false)
    private String currency = "INR";

    @Column(name = "credit_limit", precision = 15, scale = 2, nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "available_limit", precision = 15, scale = 2, nullable = false)
    private BigDecimal availableLimit;

    @Column(name = "blocked_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal blockedAmount = BigDecimal.ZERO;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public CardAccountEntity() {}

    public CardAccountEntity(String id, CardEntity card, String currency, BigDecimal creditLimit, BigDecimal availableLimit, BigDecimal blockedAmount) {
        this.id = id;
        this.card = card;
        this.currency = currency != null ? currency : "INR";
        this.creditLimit = creditLimit;
        this.availableLimit = availableLimit;
        this.blockedAmount = blockedAmount != null ? blockedAmount : BigDecimal.ZERO;
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public CardEntity getCard() { return card; }
    public void setCard(CardEntity card) { this.card = card; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public BigDecimal getAvailableLimit() { return availableLimit; }
    public void setAvailableLimit(BigDecimal availableLimit) { this.availableLimit = availableLimit; }

    public BigDecimal getBlockedAmount() { return blockedAmount; }
    public void setBlockedAmount(BigDecimal blockedAmount) { this.blockedAmount = blockedAmount; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

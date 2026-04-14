package com.nexapay.card.entity;

import com.nexapay.common.enums.CardNetwork;
import com.nexapay.common.enums.CardStatus;
import com.nexapay.common.enums.CardType;
import com.nexapay.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
public class CardEntity {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "card_number_masked", length = 19, nullable = false)
    private String cardNumberMasked;

    @Column(name = "card_number_hash", length = 64, nullable = false, unique = true)
    private String cardNumberHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", length = 20, nullable = false)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_network", length = 20, nullable = false)
    private CardNetwork cardNetwork;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private CardStatus status;

    @Column(name = "daily_limit", precision = 15, scale = 2, nullable = false)
    private BigDecimal dailyLimit;

    @Column(name = "monthly_limit", precision = 15, scale = 2, nullable = false)
    private BigDecimal monthlyLimit;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToOne(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CardAccountEntity account;

    public CardEntity() {}

    public CardEntity(String id, CustomerEntity customer, String cardNumberMasked, String cardNumberHash,
                      CardType cardType, CardNetwork cardNetwork, LocalDate expirationDate,
                      CardStatus status, BigDecimal dailyLimit, BigDecimal monthlyLimit) {
        this.id = id;
        this.customer = customer;
        this.cardNumberMasked = cardNumberMasked;
        this.cardNumberHash = cardNumberHash;
        this.cardType = cardType;
        this.cardNetwork = cardNetwork;
        this.expirationDate = expirationDate;
        this.status = status;
        this.dailyLimit = dailyLimit;
        this.monthlyLimit = monthlyLimit;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public CustomerEntity getCustomer() { return customer; }
    public void setCustomer(CustomerEntity customer) { this.customer = customer; }

    public String getCardNumberMasked() { return cardNumberMasked; }
    public void setCardNumberMasked(String cardNumberMasked) { this.cardNumberMasked = cardNumberMasked; }

    public String getCardNumberHash() { return cardNumberHash; }
    public void setCardNumberHash(String cardNumberHash) { this.cardNumberHash = cardNumberHash; }

    public CardType getCardType() { return cardType; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }

    public CardNetwork getCardNetwork() { return cardNetwork; }
    public void setCardNetwork(CardNetwork cardNetwork) { this.cardNetwork = cardNetwork; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }

    public BigDecimal getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(BigDecimal dailyLimit) { this.dailyLimit = dailyLimit; }

    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public CardAccountEntity getAccount() { return account; }
    public void setAccount(CardAccountEntity account) { this.account = account; }
}

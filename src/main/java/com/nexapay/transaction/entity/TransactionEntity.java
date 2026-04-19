package com.nexapay.transaction.entity;

import com.nexapay.card.entity.CardEntity;
import com.nexapay.common.enums.TransactionChannel;
import com.nexapay.common.enums.TransactionStatus;
import com.nexapay.merchant.entity.MerchantEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "transaction_ref", length = 30, nullable = false, unique = true)
    private String transactionRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private MerchantEntity merchant;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(length = 3, nullable = false)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TransactionChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private TransactionStatus status;

    @Column(name = "location_city", length = 50)
    private String locationCity;

    @Column(name = "location_country", length = 2, nullable = false)
    private String locationCountry = "IN";

    @Column(name = "device_fingerprint", length = 64)
    private String deviceFingerprint;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AuthorizationEntity authorization;

    public TransactionEntity() {}

    public TransactionEntity(String id, String transactionRef, CardEntity card, MerchantEntity merchant,
                             BigDecimal amount, String currency, TransactionChannel channel,
                             TransactionStatus status, String locationCity, String locationCountry,
                             String deviceFingerprint, String ipAddress) {
        this.id = id;
        this.transactionRef = transactionRef;
        this.card = card;
        this.merchant = merchant;
        this.amount = amount;
        this.currency = currency != null ? currency : "INR";
        this.channel = channel;
        this.status = status;
        this.locationCity = locationCity;
        this.locationCountry = locationCountry != null ? locationCountry : "IN";
        this.deviceFingerprint = deviceFingerprint;
        this.ipAddress = ipAddress;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public CardEntity getCard() { return card; }
    public void setCard(CardEntity card) { this.card = card; }

    public MerchantEntity getMerchant() { return merchant; }
    public void setMerchant(MerchantEntity merchant) { this.merchant = merchant; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public TransactionChannel getChannel() { return channel; }
    public void setChannel(TransactionChannel channel) { this.channel = channel; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public String getLocationCity() { return locationCity; }
    public void setLocationCity(String locationCity) { this.locationCity = locationCity; }

    public String getLocationCountry() { return locationCountry; }
    public void setLocationCountry(String locationCountry) { this.locationCountry = locationCountry; }

    public String getDeviceFingerprint() { return deviceFingerprint; }
    public void setDeviceFingerprint(String deviceFingerprint) { this.deviceFingerprint = deviceFingerprint; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public AuthorizationEntity getAuthorization() { return authorization; }
    public void setAuthorization(AuthorizationEntity authorization) { this.authorization = authorization; }
}

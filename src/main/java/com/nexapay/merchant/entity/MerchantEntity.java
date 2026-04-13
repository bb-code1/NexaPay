package com.nexapay.merchant.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "merchants")
public class MerchantEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "merchant_code", length = 20, nullable = false, unique = true)
    private String merchantCode;

    @Column(length = 100, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mcc_code", nullable = false)
    private MerchantCategoryEntity category;

    @Column(length = 2, nullable = false)
    private String country = "IN";

    @Column(name = "settlement_account_id", length = 36, nullable = false)
    private String settlementAccountId;

    @Column(length = 20, nullable = false)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public MerchantEntity() {}

    public MerchantEntity(String id, String merchantCode, String name, MerchantCategoryEntity category, String country, String settlementAccountId, String status) {
        this.id = id;
        this.merchantCode = merchantCode;
        this.name = name;
        this.category = category;
        this.country = country != null ? country : "IN";
        this.settlementAccountId = settlementAccountId;
        this.status = status != null ? status : "ACTIVE";
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMerchantCode() { return merchantCode; }
    public void setMerchantCode(String merchantCode) { this.merchantCode = merchantCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MerchantCategoryEntity getCategory() { return category; }
    public void setCategory(MerchantCategoryEntity category) { this.category = category; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getSettlementAccountId() { return settlementAccountId; }
    public void setSettlementAccountId(String settlementAccountId) { this.settlementAccountId = settlementAccountId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

package com.nexapay.merchant.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "merchant_categories")
public class MerchantCategoryEntity {

    @Id
    @Column(length = 4)
    private String code;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "risk_weight", precision = 3, scale = 2, nullable = false)
    private BigDecimal riskWeight = BigDecimal.ONE;

    @Column(name = "is_restricted", nullable = false)
    private boolean isRestricted = false;

    public MerchantCategoryEntity() {}

    public MerchantCategoryEntity(String code, String name, BigDecimal riskWeight, boolean isRestricted) {
        this.code = code;
        this.name = name;
        this.riskWeight = riskWeight != null ? riskWeight : BigDecimal.ONE;
        this.isRestricted = isRestricted;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getRiskWeight() { return riskWeight; }
    public void setRiskWeight(BigDecimal riskWeight) { this.riskWeight = riskWeight; }

    public boolean isRestricted() { return isRestricted; }
    public void setRestricted(boolean restricted) { isRestricted = restricted; }
}

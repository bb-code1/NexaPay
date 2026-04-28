package com.nexapay.settlement.entity;

import com.nexapay.common.enums.SettlementBatchStatus;
import com.nexapay.merchant.entity.MerchantEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "settlement_batches")
public class SettlementBatchEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "batch_ref", length = 30, nullable = false, unique = true)
    private String batchRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private MerchantEntity merchant;

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private SettlementBatchStatus status;

    @Column(name = "cleared_at")
    private Instant clearedAt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SettlementItemEntity> items = new ArrayList<>();

    public SettlementBatchEntity() {}

    public SettlementBatchEntity(String id, String batchRef, MerchantEntity merchant, int totalCount, BigDecimal totalAmount, SettlementBatchStatus status) {
        this.id = id;
        this.batchRef = batchRef;
        this.merchant = merchant;
        this.totalCount = totalCount;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBatchRef() { return batchRef; }
    public void setBatchRef(String batchRef) { this.batchRef = batchRef; }

    public MerchantEntity getMerchant() { return merchant; }
    public void setMerchant(MerchantEntity merchant) { this.merchant = merchant; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public SettlementBatchStatus getStatus() { return status; }
    public void setStatus(SettlementBatchStatus status) { this.status = status; }

    public Instant getClearedAt() { return clearedAt; }
    public void setClearedAt(Instant clearedAt) { this.clearedAt = clearedAt; }

    public List<SettlementItemEntity> getItems() { return items; }
    public void setItems(List<SettlementItemEntity> items) { this.items = items; }
}

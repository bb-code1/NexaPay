package com.nexapay.ledger.entity;

import com.nexapay.common.enums.EntryType;
import com.nexapay.common.enums.LedgerAccountType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntryEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "journal_batch_id", length = 36, nullable = false)
    private String journalBatchId;

    @Column(name = "transaction_ref", length = 30, nullable = false)
    private String transactionRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", length = 40, nullable = false)
    private LedgerAccountType accountType;

    @Column(name = "account_id", length = 36, nullable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", length = 6, nullable = false)
    private EntryType entryType;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(length = 3, nullable = false)
    private String currency = "INR";

    @Column(name = "posted_at", nullable = false)
    private Instant postedAt = Instant.now();

    @Column(length = 255, nullable = false)
    private String description;

    public LedgerEntryEntity() {}

    public LedgerEntryEntity(String id, String journalBatchId, String transactionRef, LedgerAccountType accountType,
                             String accountId, EntryType entryType, BigDecimal amount, String currency, String description) {
        this.id = id;
        this.journalBatchId = journalBatchId;
        this.transactionRef = transactionRef;
        this.accountType = accountType;
        this.accountId = accountId;
        this.entryType = entryType;
        this.amount = amount;
        this.currency = currency != null ? currency : "INR";
        this.description = description;
        this.postedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJournalBatchId() { return journalBatchId; }
    public void setJournalBatchId(String journalBatchId) { this.journalBatchId = journalBatchId; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public LedgerAccountType getAccountType() { return accountType; }
    public void setAccountType(LedgerAccountType accountType) { this.accountType = accountType; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public EntryType getEntryType() { return entryType; }
    public void setEntryType(EntryType entryType) { this.entryType = entryType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getPostedAt() { return postedAt; }
    public void setPostedAt(Instant postedAt) { this.postedAt = postedAt; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

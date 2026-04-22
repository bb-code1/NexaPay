package com.nexapay.ledger.service;

import com.nexapay.common.enums.EntryType;
import com.nexapay.common.enums.LedgerAccountType;
import com.nexapay.ledger.entity.LedgerEntryEntity;
import com.nexapay.ledger.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class DoubleEntryLedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;

    public DoubleEntryLedgerService(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    public record LedgerPosting(
            LedgerAccountType accountType,
            String accountId,
            EntryType entryType,
            BigDecimal amount,
            String currency,
            String description
    ) {}

    @Transactional
    public String postJournalBatch(String transactionRef, List<LedgerPosting> postings) {
        if (postings == null || postings.isEmpty()) {
            throw new IllegalArgumentException("Postings list cannot be empty");
        }

        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (LedgerPosting p : postings) {
            if (p.amount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Posting amount must be strictly positive");
            }
            if (p.entryType() == EntryType.DEBIT) {
                totalDebits = totalDebits.add(p.amount());
            } else if (p.entryType() == EntryType.CREDIT) {
                totalCredits = totalCredits.add(p.amount());
            }
        }

        // Enforce Double-Entry Invariant Rule
        if (totalDebits.compareTo(totalCredits) != 0) {
            throw new IllegalStateException(String.format(
                    "Ledger journal imbalance! Total Debits (%s) != Total Credits (%s)",
                    totalDebits, totalCredits
            ));
        }

        String journalBatchId = "JB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        for (LedgerPosting p : postings) {
            LedgerEntryEntity entry = new LedgerEntryEntity(
                    UUID.randomUUID().toString(),
                    journalBatchId,
                    transactionRef,
                    p.accountType(),
                    p.accountId(),
                    p.entryType(),
                    p.amount(),
                    p.currency() != null ? p.currency() : "INR",
                    p.description()
            );
            ledgerEntryRepository.save(entry);
        }

        return journalBatchId;
    }

    public List<LedgerEntryEntity> getEntriesForBatch(String journalBatchId) {
        return ledgerEntryRepository.findByJournalBatchId(journalBatchId);
    }

    public List<LedgerEntryEntity> getEntriesForTransaction(String transactionRef) {
        return ledgerEntryRepository.findByTransactionRef(transactionRef);
    }
}

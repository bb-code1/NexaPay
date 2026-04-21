package com.nexapay.ledger.repository;

import com.nexapay.ledger.entity.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, String> {
    List<LedgerEntryEntity> findByJournalBatchId(String journalBatchId);
    List<LedgerEntryEntity> findByTransactionRef(String transactionRef);
    List<LedgerEntryEntity> findByAccountIdOrderByPostedAtDesc(String accountId);

    @Query("SELECT COUNT(l) FROM LedgerEntryEntity l WHERE l.journalBatchId = :batchId")
    long countEntriesInBatch(@Param("batchId") String batchId);
}

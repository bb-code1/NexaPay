package com.nexapay.transaction.repository;

import com.nexapay.transaction.entity.TransactionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    Optional<TransactionEntity> findByTransactionRef(String transactionRef);
    List<TransactionEntity> findByCardIdOrderByCreatedAtDesc(String cardId, Pageable pageable);
    List<TransactionEntity> findByCardCustomerIdOrderByCreatedAtDesc(String customerId, Pageable pageable);
    long countByCardId(String cardId);
}

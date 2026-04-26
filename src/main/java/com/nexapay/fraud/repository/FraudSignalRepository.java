package com.nexapay.fraud.repository;

import com.nexapay.fraud.entity.FraudSignalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudSignalRepository extends JpaRepository<FraudSignalEntity, String> {
    List<FraudSignalEntity> findByTransactionId(String transactionId);
    List<FraudSignalEntity> findByTransactionTransactionRef(String transactionRef);
}

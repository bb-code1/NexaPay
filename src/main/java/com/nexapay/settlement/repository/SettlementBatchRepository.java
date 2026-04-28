package com.nexapay.settlement.repository;

import com.nexapay.settlement.entity.SettlementBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettlementBatchRepository extends JpaRepository<SettlementBatchEntity, String> {
    Optional<SettlementBatchEntity> findByBatchRef(String batchRef);
}

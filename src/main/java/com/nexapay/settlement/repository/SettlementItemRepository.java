package com.nexapay.settlement.repository;

import com.nexapay.settlement.entity.SettlementItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementItemRepository extends JpaRepository<SettlementItemEntity, String> {
    Optional<SettlementItemEntity> findByPaymentId(String paymentId);
    Optional<SettlementItemEntity> findByPaymentPaymentRef(String paymentRef);
    List<SettlementItemEntity> findByBatchId(String batchId);
    List<SettlementItemEntity> findByBatchIdAndHasMismatchTrue(String batchId);
}

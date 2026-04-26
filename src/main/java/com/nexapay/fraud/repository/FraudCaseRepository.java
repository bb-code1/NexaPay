package com.nexapay.fraud.repository;

import com.nexapay.common.enums.FraudCaseStatus;
import com.nexapay.fraud.entity.FraudCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FraudCaseRepository extends JpaRepository<FraudCaseEntity, String> {
    Optional<FraudCaseEntity> findByCaseRef(String caseRef);
    List<FraudCaseEntity> findByCardId(String cardId);
    List<FraudCaseEntity> findByCardIdAndStatus(String cardId, FraudCaseStatus status);
}

package com.nexapay.chargeback.repository;

import com.nexapay.chargeback.entity.ChargebackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChargebackRepository extends JpaRepository<ChargebackEntity, String> {
    Optional<ChargebackEntity> findByDisputeRef(String disputeRef);
    Optional<ChargebackEntity> findByPaymentPaymentRef(String paymentRef);
}

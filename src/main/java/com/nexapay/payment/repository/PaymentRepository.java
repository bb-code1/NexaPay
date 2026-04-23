package com.nexapay.payment.repository;

import com.nexapay.common.enums.PaymentStatus;
import com.nexapay.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
    Optional<PaymentEntity> findByPaymentRef(String paymentRef);
    Optional<PaymentEntity> findByIdempotencyKey(String idempotencyKey);
    List<PaymentEntity> findByStatus(PaymentStatus status);
    Optional<PaymentEntity> findByTransactionTransactionRef(String transactionRef);
}

package com.nexapay.payment.repository;

import com.nexapay.payment.entity.PaymentAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentAttemptRepository extends JpaRepository<PaymentAttemptEntity, String> {
    List<PaymentAttemptEntity> findByPaymentIdOrderByAttemptNumberAsc(String paymentId);
}

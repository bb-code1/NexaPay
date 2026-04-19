package com.nexapay.transaction.repository;

import com.nexapay.transaction.entity.AuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorizationRepository extends JpaRepository<AuthorizationEntity, String> {
    Optional<AuthorizationEntity> findByTransactionId(String transactionId);
    Optional<AuthorizationEntity> findByTransactionTransactionRef(String transactionRef);
}

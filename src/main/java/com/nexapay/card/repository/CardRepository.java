package com.nexapay.card.repository;

import com.nexapay.card.entity.CardEntity;
import com.nexapay.common.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, String> {
    Optional<CardEntity> findByCardNumberHash(String cardNumberHash);
    List<CardEntity> findByCustomerId(String customerId);
    List<CardEntity> findByCustomerIdAndStatus(String customerId, CardStatus status);
}

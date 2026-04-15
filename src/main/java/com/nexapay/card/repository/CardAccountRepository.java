package com.nexapay.card.repository;

import com.nexapay.card.entity.CardAccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardAccountRepository extends JpaRepository<CardAccountEntity, String> {

    Optional<CardAccountEntity> findByCardId(String cardId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ca FROM CardAccountEntity ca WHERE ca.card.id = :cardId")
    Optional<CardAccountEntity> findByCardIdForUpdate(@Param("cardId") String cardId);
}

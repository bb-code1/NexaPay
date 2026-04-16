package com.nexapay.card.service;

import com.nexapay.card.entity.CardAccountEntity;
import com.nexapay.card.entity.CardEntity;
import com.nexapay.card.repository.CardAccountRepository;
import com.nexapay.card.repository.CardRepository;
import com.nexapay.common.enums.CardStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardAccountRepository cardAccountRepository;

    public CardService(CardRepository cardRepository, CardAccountRepository cardAccountRepository) {
        this.cardRepository = cardRepository;
        this.cardAccountRepository = cardAccountRepository;
    }

    public Optional<CardEntity> getCardById(String cardId) {
        return cardRepository.findById(cardId);
    }

    public Optional<CardAccountEntity> getCardAccount(String cardId) {
        return cardAccountRepository.findByCardId(cardId);
    }

    @Transactional
    public boolean reserveHold(String cardId, BigDecimal amount) {
        CardAccountEntity account = cardAccountRepository.findByCardIdForUpdate(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card account not found: " + cardId));

        if (account.getAvailableLimit().compareTo(amount) < 0) {
            return false;
        }

        account.setAvailableLimit(account.getAvailableLimit().subtract(amount));
        account.setBlockedAmount(account.getBlockedAmount().add(amount));
        cardAccountRepository.save(account);
        return true;
    }

    @Transactional
    public void releaseHold(String cardId, BigDecimal amount) {
        CardAccountEntity account = cardAccountRepository.findByCardIdForUpdate(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card account not found: " + cardId));

        account.setAvailableLimit(account.getAvailableLimit().add(amount));
        account.setBlockedAmount(account.getBlockedAmount().subtract(amount).max(BigDecimal.ZERO));
        cardAccountRepository.save(account);
    }

    @Transactional
    public void captureHold(String cardId, BigDecimal amount) {
        CardAccountEntity account = cardAccountRepository.findByCardIdForUpdate(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card account not found: " + cardId));

        account.setBlockedAmount(account.getBlockedAmount().subtract(amount).max(BigDecimal.ZERO));
        cardAccountRepository.save(account);
    }

    @Transactional
    public CardEntity updateCardStatus(String cardId, CardStatus newStatus) {
        CardEntity card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        // State Machine Invariants
        if (card.getStatus() == CardStatus.CLOSED) {
            throw new IllegalStateException("Cannot change status of a permanently CLOSED card");
        }
        if (card.getStatus() == CardStatus.EXPIRED && newStatus == CardStatus.ACTIVE) {
            throw new IllegalStateException("Cannot reactivate an EXPIRED card");
        }

        card.setStatus(newStatus);
        return cardRepository.save(card);
    }
}

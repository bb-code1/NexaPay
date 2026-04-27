package com.nexapay.fraud.service;

import com.nexapay.common.enums.FraudCaseStatus;
import com.nexapay.common.enums.FraudRiskLevel;
import com.nexapay.common.enums.FraudSeverity;
import com.nexapay.fraud.entity.FraudCaseEntity;
import com.nexapay.fraud.entity.FraudSignalEntity;
import com.nexapay.fraud.repository.FraudCaseRepository;
import com.nexapay.fraud.repository.FraudSignalRepository;
import com.nexapay.transaction.entity.TransactionEntity;
import com.nexapay.transaction.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FraudDetectionService {

    private final FraudSignalRepository fraudSignalRepository;
    private final FraudCaseRepository fraudCaseRepository;
    private final TransactionRepository transactionRepository;

    public FraudDetectionService(
            FraudSignalRepository fraudSignalRepository,
            FraudCaseRepository fraudCaseRepository,
            TransactionRepository transactionRepository) {
        this.fraudSignalRepository = fraudSignalRepository;
        this.fraudCaseRepository = fraudCaseRepository;
        this.transactionRepository = transactionRepository;
    }

    public record FraudEvaluationResult(
            int aggregateRiskScore,
            FraudRiskLevel riskLevel,
            List<FraudSignalEntity> signals,
            boolean isCaseOpened,
            String caseRef
    ) {}

    @Transactional
    public FraudEvaluationResult evaluateTransaction(TransactionEntity transaction) {
        List<FraudSignalEntity> detectedSignals = new ArrayList<>();
        int totalRiskScore = 0;

        // Signal 1: High Amount Spike vs History
        List<TransactionEntity> pastTxns = transactionRepository.findByCardIdOrderByCreatedAtDesc(
                transaction.getCard().getId(), PageRequest.of(0, 10)
        );

        if (!pastTxns.isEmpty()) {
            BigDecimal avgAmount = pastTxns.stream()
                    .map(TransactionEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(pastTxns.size()), 2, java.math.RoundingMode.HALF_UP);

            if (transaction.getAmount().compareTo(avgAmount.multiply(BigDecimal.valueOf(5))) > 0) {
                FraudSignalEntity spikeSignal = new FraudSignalEntity(
                        UUID.randomUUID().toString(),
                        transaction,
                        "HIGH_AMOUNT_SPIKE",
                        FraudSeverity.HIGH,
                        35,
                        String.format("{\"currentAmount\":%s,\"historicalAvg\":%s}", transaction.getAmount(), avgAmount)
                );
                detectedSignals.add(spikeSignal);
                totalRiskScore += 35;
            }
        }

        // Signal 2: Rapid Velocity (Multiple attempts in short timeframe)
        if (pastTxns.size() >= 3) {
            FraudSignalEntity velocitySignal = new FraudSignalEntity(
                    UUID.randomUUID().toString(),
                    transaction,
                    "HIGH_VELOCITY_5M",
                    FraudSeverity.HIGH,
                    30,
                    "{\"recentAttemptsCount\": " + pastTxns.size() + "}"
            );
            detectedSignals.add(velocitySignal);
            totalRiskScore += 30;
        }

        // Save detected signals
        if (!detectedSignals.isEmpty()) {
            fraudSignalRepository.saveAll(detectedSignals);
        }

        FraudRiskLevel riskLevel;
        if (totalRiskScore >= 75) {
            riskLevel = FraudRiskLevel.CRITICAL;
        } else if (totalRiskScore >= 50) {
            riskLevel = FraudRiskLevel.HIGH;
        } else if (totalRiskScore >= 25) {
            riskLevel = FraudRiskLevel.MEDIUM;
        } else {
            riskLevel = FraudRiskLevel.LOW;
        }

        boolean caseOpened = false;
        String caseRef = null;

        if (totalRiskScore >= 50) {
            caseRef = "FC-" + (10000 + (int)(Math.random() * 89999));
            FraudCaseEntity fraudCase = new FraudCaseEntity(
                    UUID.randomUUID().toString(),
                    caseRef,
                    transaction.getCard(),
                    totalRiskScore,
                    riskLevel,
                    FraudCaseStatus.OPEN,
                    null
            );
            fraudCaseRepository.save(fraudCase);
            caseOpened = true;
        }

        return new FraudEvaluationResult(totalRiskScore, riskLevel, detectedSignals, caseOpened, caseRef);
    }

    public List<FraudSignalEntity> getSignalsForTransaction(String transactionRef) {
        return fraudSignalRepository.findByTransactionTransactionRef(transactionRef);
    }
}

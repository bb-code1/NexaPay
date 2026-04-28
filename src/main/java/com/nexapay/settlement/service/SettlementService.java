package com.nexapay.settlement.service;

import com.nexapay.common.enums.SettlementBatchStatus;
import com.nexapay.merchant.entity.MerchantEntity;
import com.nexapay.payment.entity.PaymentEntity;
import com.nexapay.settlement.entity.SettlementBatchEntity;
import com.nexapay.settlement.entity.SettlementItemEntity;
import com.nexapay.settlement.repository.SettlementBatchRepository;
import com.nexapay.settlement.repository.SettlementItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SettlementService {

    private final SettlementBatchRepository settlementBatchRepository;
    private final SettlementItemRepository settlementItemRepository;

    public SettlementService(
            SettlementBatchRepository settlementBatchRepository,
            SettlementItemRepository settlementItemRepository) {
        this.settlementBatchRepository = settlementBatchRepository;
        this.settlementItemRepository = settlementItemRepository;
    }

    public record SettlementReconciliationReport(
            String batchRef,
            int totalItems,
            BigDecimal totalAmount,
            boolean hasDiscrepancies,
            List<SettlementItemEntity> mismatchedItems
    ) {}

    @Transactional
    public SettlementBatchEntity processClearingBatch(MerchantEntity merchant, List<PaymentEntity> payments) {
        String batchRef = "SETTLE-BATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BigDecimal totalAmount = payments.stream()
                .map(PaymentEntity::getCapturedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SettlementBatchEntity batch = new SettlementBatchEntity(
                UUID.randomUUID().toString(),
                batchRef,
                merchant,
                payments.size(),
                totalAmount,
                SettlementBatchStatus.SETTLED
        );
        batch.setClearedAt(Instant.now());
        settlementBatchRepository.save(batch);

        for (PaymentEntity payment : payments) {
            SettlementItemEntity item = new SettlementItemEntity(
                    UUID.randomUUID().toString(),
                    batch,
                    payment,
                    payment.getCapturedAmount(),
                    payment.getCapturedAmount(),
                    false,
                    null
            );
            settlementItemRepository.save(item);
        }

        return batch;
    }

    public Optional<SettlementItemEntity> getSettlementItemForPayment(String paymentRef) {
        return settlementItemRepository.findByPaymentPaymentRef(paymentRef);
    }
}

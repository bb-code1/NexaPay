package com.nexapay.generator.service;

import com.nexapay.card.entity.CardAccountEntity;
import com.nexapay.card.entity.CardEntity;
import com.nexapay.card.repository.CardAccountRepository;
import com.nexapay.card.repository.CardRepository;
import com.nexapay.common.enums.*;
import com.nexapay.customer.entity.CustomerEntity;
import com.nexapay.customer.entity.CustomerProfileEntity;
import com.nexapay.customer.repository.CustomerProfileRepository;
import com.nexapay.customer.repository.CustomerRepository;
import com.nexapay.fraud.entity.FraudSignalEntity;
import com.nexapay.fraud.repository.FraudSignalRepository;
import com.nexapay.merchant.entity.MerchantCategoryEntity;
import com.nexapay.merchant.entity.MerchantEntity;
import com.nexapay.merchant.repository.MerchantCategoryRepository;
import com.nexapay.merchant.repository.MerchantRepository;
import com.nexapay.payment.entity.PaymentAttemptEntity;
import com.nexapay.payment.entity.PaymentEntity;
import com.nexapay.payment.repository.PaymentAttemptRepository;
import com.nexapay.payment.repository.PaymentRepository;
import com.nexapay.settlement.entity.SettlementBatchEntity;
import com.nexapay.settlement.entity.SettlementItemEntity;
import com.nexapay.settlement.repository.SettlementBatchRepository;
import com.nexapay.settlement.repository.SettlementItemRepository;
import com.nexapay.transaction.entity.AuthorizationEntity;
import com.nexapay.transaction.entity.DeclineCodeEntity;
import com.nexapay.transaction.entity.TransactionEntity;
import com.nexapay.transaction.repository.AuthorizationRepository;
import com.nexapay.transaction.repository.DeclineCodeRepository;
import com.nexapay.transaction.repository.TransactionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class SyntheticDataGeneratorService {

    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final CardRepository cardRepository;
    private final CardAccountRepository cardAccountRepository;
    private final MerchantCategoryRepository merchantCategoryRepository;
    private final MerchantRepository merchantRepository;
    private final DeclineCodeRepository declineCodeRepository;
    private final TransactionRepository transactionRepository;
    private final AuthorizationRepository authorizationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final SettlementBatchRepository settlementBatchRepository;
    private final SettlementItemRepository settlementItemRepository;
    private final FraudSignalRepository fraudSignalRepository;
    private final JdbcTemplate jdbcTemplate;

    public SyntheticDataGeneratorService(
            CustomerRepository customerRepository,
            CustomerProfileRepository customerProfileRepository,
            CardRepository cardRepository,
            CardAccountRepository cardAccountRepository,
            MerchantCategoryRepository merchantCategoryRepository,
            MerchantRepository merchantRepository,
            DeclineCodeRepository declineCodeRepository,
            TransactionRepository transactionRepository,
            AuthorizationRepository authorizationRepository,
            PaymentRepository paymentRepository,
            PaymentAttemptRepository paymentAttemptRepository,
            SettlementBatchRepository settlementBatchRepository,
            SettlementItemRepository settlementItemRepository,
            FraudSignalRepository fraudSignalRepository,
            JdbcTemplate jdbcTemplate) {
        this.customerRepository = customerRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.cardRepository = cardRepository;
        this.cardAccountRepository = cardAccountRepository;
        this.merchantCategoryRepository = merchantCategoryRepository;
        this.merchantRepository = merchantRepository;
        this.declineCodeRepository = declineCodeRepository;
        this.transactionRepository = transactionRepository;
        this.authorizationRepository = authorizationRepository;
        this.paymentRepository = paymentRepository;
        this.paymentAttemptRepository = paymentAttemptRepository;
        this.settlementBatchRepository = settlementBatchRepository;
        this.settlementItemRepository = settlementItemRepository;
        this.fraudSignalRepository = fraudSignalRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public record SeedResult(
            int customersCount,
            int cardsCount,
            int merchantsCount,
            int transactionsCount,
            int scenariosCount,
            long executionTimeMs
    ) {}

    @Transactional
    public SeedResult seedBaselineAndScenarios() {
        long startTime = System.currentTimeMillis();

        if (customerRepository.existsByCustomerNumber("CUST-10293")) {
            return new SeedResult(1, 3, 2, 5, 5, 0);
        }

        // 1. Seed Decline Codes
        seedDeclineCodes();

        // 2. Seed Merchant Categories & Merchants
        MerchantCategoryEntity retailMcc = merchantCategoryRepository.save(new MerchantCategoryEntity("5411", "Grocery & Supermarkets", BigDecimal.valueOf(1.0), false));
        MerchantCategoryEntity techMcc = merchantCategoryRepository.save(new MerchantCategoryEntity("5732", "Electronics Stores", BigDecimal.valueOf(1.2), false));
        MerchantCategoryEntity gamblingMcc = merchantCategoryRepository.save(new MerchantCategoryEntity("7995", "Gambling & Betting", BigDecimal.valueOf(3.0), true));

        MerchantEntity electronicsStore = merchantRepository.save(new MerchantEntity(
                "MERCH-9102", "MERCH-9102", "Electronics Megastore", techMcc, "IN", "ACC-MERCH-9102", "ACTIVE"
        ));
        MerchantEntity groceryStore = merchantRepository.save(new MerchantEntity(
                "MERCH-3301", "MERCH-3301", "City Fresh Supermarket", retailMcc, "IN", "ACC-MERCH-3301", "ACTIVE"
        ));

        // 3. Seed Benchmark Customer & Cards
        CustomerEntity primaryCustomer = customerRepository.save(new CustomerEntity(
                "CUST-10293", "CUST-10293", "Aarav", "Sharma", "aarav.sharma@example.com", "+919876543210", KycTier.TIER_2_VERIFIED, RiskCategory.LOW
        ));
        customerProfileRepository.save(new CustomerProfileEntity(
                UUID.randomUUID().toString(), primaryCustomer, BigDecimal.valueOf(120000), 780, "Mumbai", "IN"
        ));

        // Card 1: CARD-8293 (Active, Limit: ₹50,000, Available: ₹18,200) -> Used in TXN-84721
        CardEntity card8293 = cardRepository.save(new CardEntity(
                "CARD-8293", primaryCustomer, "**** **** **** 8293", "hash_8293_sha256",
                CardType.CREDIT, CardNetwork.VISA, LocalDate.of(2028, 12, 31),
                CardStatus.ACTIVE, BigDecimal.valueOf(50000), BigDecimal.valueOf(150000)
        ));
        cardAccountRepository.save(new CardAccountEntity(
                "ACC-CARD-8293", card8293, "INR", BigDecimal.valueOf(50000), BigDecimal.valueOf(18200), BigDecimal.ZERO
        ));

        // Card 2: CARD-4019 (Expired) -> Used in TXN-10928
        CardEntity card4019 = cardRepository.save(new CardEntity(
                "CARD-4019", primaryCustomer, "**** **** **** 4019", "hash_4019_sha256",
                CardType.CREDIT, CardNetwork.MASTERCARD, LocalDate.of(2025, 6, 30),
                CardStatus.EXPIRED, BigDecimal.valueOf(30000), BigDecimal.valueOf(100000)
        ));
        cardAccountRepository.save(new CardAccountEntity(
                "ACC-CARD-4019", card4019, "INR", BigDecimal.valueOf(30000), BigDecimal.valueOf(30000), BigDecimal.ZERO
        ));

        // Card 3: CARD-3391 (Blocked) -> Used in TXN-99120
        CardEntity card3391 = cardRepository.save(new CardEntity(
                "CARD-3391", primaryCustomer, "**** **** **** 3391", "hash_3391_sha256",
                CardType.CREDIT, CardNetwork.VISA, LocalDate.of(2027, 10, 31),
                CardStatus.BLOCKED, BigDecimal.valueOf(40000), BigDecimal.valueOf(120000)
        ));
        cardAccountRepository.save(new CardAccountEntity(
                "ACC-CARD-3391", card3391, "INR", BigDecimal.valueOf(40000), BigDecimal.valueOf(40000), BigDecimal.ZERO
        ));

        // 4. Inject Ground-Truth Scenario Transactions

        // Scenario 1: TXN-84721 Declined due to Limit Breach (₹21,500 vs ₹18,200 available)
        TransactionEntity txn84721 = transactionRepository.save(new TransactionEntity(
                "TXN-84721", "TXN-84721", card8293, electronicsStore, BigDecimal.valueOf(21500), "INR",
                TransactionChannel.POS_CHIP_PIN, TransactionStatus.DECLINED, "Mumbai", "IN", "dev_fp_9921", "103.21.45.1"
        ));
        DeclineCodeEntity code51 = declineCodeRepository.findById("51").orElse(null);
        authorizationRepository.save(new AuthorizationEntity(
                UUID.randomUUID().toString(), txn84721, null, false, code51,
                "Insufficient Funds / Credit Limit Exceeded",
                "[{\"rule\":\"AVAILABLE_LIMIT_CHECK\",\"result\":\"FAIL\",\"available\":18200,\"requested\":21500}]"
        ));

        // Scenario 2: TXN-10928 Declined due to Expired Card
        TransactionEntity txn10928 = transactionRepository.save(new TransactionEntity(
                "TXN-10928", "TXN-10928", card4019, groceryStore, BigDecimal.valueOf(1250), "INR",
                TransactionChannel.POS_CONTACTLESS, TransactionStatus.DECLINED, "Mumbai", "IN", "dev_fp_4019", "103.21.45.2"
        ));
        DeclineCodeEntity code54 = declineCodeRepository.findById("54").orElse(null);
        authorizationRepository.save(new AuthorizationEntity(
                UUID.randomUUID().toString(), txn10928, null, false, code54,
                "Expired Card",
                "[{\"rule\":\"EXPIRY_DATE_CHECK\",\"result\":\"FAIL\",\"expirationDate\":\"2025-06-30\"}]"
        ));

        // Scenario 3: TXN-99120 Declined due to Blocked Card
        TransactionEntity txn99120 = transactionRepository.save(new TransactionEntity(
                "TXN-99120", "TXN-99120", card3391, electronicsStore, BigDecimal.valueOf(8500), "INR",
                TransactionChannel.ECOMMERCE_3DS, TransactionStatus.DECLINED, "Delhi", "IN", "dev_fp_3391", "103.21.45.3"
        ));
        DeclineCodeEntity code14 = declineCodeRepository.findById("14").orElse(null);
        authorizationRepository.save(new AuthorizationEntity(
                UUID.randomUUID().toString(), txn99120, null, false, code14,
                "Invalid / Blocked Card",
                "[{\"rule\":\"CARD_STATUS_CHECK\",\"result\":\"FAIL\",\"currentStatus\":\"BLOCKED\"}]"
        ));

        // Scenario 4: PAY-9321 Settlement Discrepancy (Captured: ₹5,000, Settled: ₹4,800)
        TransactionEntity txn9321 = transactionRepository.save(new TransactionEntity(
                "TXN-9321", "TXN-9321", card8293, electronicsStore, BigDecimal.valueOf(5000), "INR",
                TransactionChannel.POS_CHIP_PIN, TransactionStatus.CAPTURED, "Mumbai", "IN", "dev_fp_9321", "103.21.45.4"
        ));
        PaymentEntity pay9321 = paymentRepository.save(new PaymentEntity(
                "PAY-9321", "PAY-9321", txn9321, "idem-9321-uuid", BigDecimal.valueOf(5000), "INR", PaymentStatus.CAPTURED
        ));
        pay9321.setCapturedAt(Instant.now().minusSeconds(86400));
        paymentRepository.save(pay9321);

        SettlementBatchEntity batch01 = settlementBatchRepository.save(new SettlementBatchEntity(
                "SETTLE-BATCH-01", "SETTLE-BATCH-01", electronicsStore, 1, BigDecimal.valueOf(4800), SettlementBatchStatus.DISCREPANCY
        ));
        batch01.setClearedAt(Instant.now());
        settlementBatchRepository.save(batch01);

        settlementItemRepository.save(new SettlementItemEntity(
                UUID.randomUUID().toString(), batch01, pay9321, BigDecimal.valueOf(5000), BigDecimal.valueOf(4800),
                true, "Settlement amount ₹4,800.00 differs from captured amount ₹5,000.00 without interchange fee line item"
        ));

        // Scenario 5: PAY-3301 Gateway Timeout Recovered After Retry
        TransactionEntity txn3301 = transactionRepository.save(new TransactionEntity(
                "TXN-3301", "TXN-3301", card8293, groceryStore, BigDecimal.valueOf(3200), "INR",
                TransactionChannel.ECOMMERCE_3DS, TransactionStatus.CAPTURED, "Mumbai", "IN", "dev_fp_3301", "103.21.45.5"
        ));
        PaymentEntity pay3301 = paymentRepository.save(new PaymentEntity(
                "PAY-3301", "PAY-3301", txn3301, "idem-3301-uuid", BigDecimal.valueOf(3200), "INR", PaymentStatus.CAPTURED
        ));
        pay3301.setCapturedAt(Instant.now().minusSeconds(3600));
        paymentRepository.save(pay3301);

        paymentAttemptRepository.save(new PaymentAttemptEntity(
                UUID.randomUUID().toString(), pay3301, 1, "91", "{\"status\":\"TIMEOUT\"}", "FAILED"
        ));
        paymentAttemptRepository.save(new PaymentAttemptEntity(
                UUID.randomUUID().toString(), pay3301, 2, "00", "{\"status\":\"SUCCESS\"}", "CAPTURED"
        ));

        // Scenario 5: TXN-90124 Fraud High Velocity & Spike
        TransactionEntity txn90124 = transactionRepository.save(new TransactionEntity(
                "TXN-90124", "TXN-90124", card8293, electronicsStore, BigDecimal.valueOf(48500), "INR",
                TransactionChannel.ECOMMERCE_3DS, TransactionStatus.DECLINED, "Bangkok", "TH", "dev_fp_anomaly", "190.45.12.8"
        ));
        fraudSignalRepository.saveAll(List.of(
                new FraudSignalEntity(UUID.randomUUID().toString(), txn90124, "HIGH_AMOUNT_SPIKE", FraudSeverity.HIGH, 35, "{\"spikeMultiplier\": 12.5}"),
                new FraudSignalEntity(UUID.randomUUID().toString(), txn90124, "HIGH_VELOCITY_5M", FraudSeverity.HIGH, 30, "{\"velocityCount\": 4}"),
                new FraudSignalEntity(UUID.randomUUID().toString(), txn90124, "UNUSUAL_LOCATION_GEO", FraudSeverity.MEDIUM, 23, "{\"foreignCountry\": \"TH\"}")
        ));

        long duration = System.currentTimeMillis() - startTime;
        return new SeedResult(1, 3, 2, 5, 5, duration);
    }

    private void seedDeclineCodes() {
        if (declineCodeRepository.count() == 0) {
            declineCodeRepository.saveAll(List.of(
                    new DeclineCodeEntity("05", "Do Not Honor", DeclineCategory.SYSTEM_ERROR, "Transaction was declined by the issuer."),
                    new DeclineCodeEntity("14", "Invalid / Blocked Card", DeclineCategory.CARD_STATUS, "The card is invalid, blocked, or closed."),
                    new DeclineCodeEntity("51", "Insufficient Funds / Credit Limit Exceeded", DeclineCategory.INSUFFICIENT_FUNDS, "Requested amount exceeds available credit line."),
                    new DeclineCodeEntity("54", "Expired Card", DeclineCategory.CARD_STATUS, "The card expiration date has passed."),
                    new DeclineCodeEntity("57", "Transaction Not Permitted", DeclineCategory.SECURITY_FRAUD, "Transaction is not allowed for this merchant category."),
                    new DeclineCodeEntity("61", "Exceeds Limit", DeclineCategory.INSUFFICIENT_FUNDS, "Transaction exceeds single transaction or daily limit ceiling."),
                    new DeclineCodeEntity("75", "PIN Tries Exceeded", DeclineCategory.SECURITY_FRAUD, "Allowed PIN entry attempts exceeded."),
                    new DeclineCodeEntity("91", "Issuer Unavailable", DeclineCategory.SYSTEM_ERROR, "Issuer authorization gateway is temporarily unavailable.")
            ));
        }
    }
}

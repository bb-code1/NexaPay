package com.nexapay.generator.registry;

import com.nexapay.generator.model.BenchmarkScenario;

import java.util.List;
import java.util.Map;

public class BenchmarkRegistry {

    public static final Map<String, BenchmarkScenario> SCENARIOS = Map.ofEntries(
            Map.entry("CARD_LIMIT_BREACH_001", new BenchmarkScenario(
                    "CARD_LIMIT_BREACH_001",
                    "Why was transaction TXN-84721 declined?",
                    "TXN-84721",
                    "CONFIRMED_DECLINE",
                    "insufficient",
                    List.of("getTransactionDetails", "getCardLedgerSummary"),
                    List.of("card-limit-policy.md"),
                    "ADVISE_REPAYMENT_OR_LIMIT_RAISE"
            )),
            Map.entry("CARD_EXPIRED_SWIPE_002", new BenchmarkScenario(
                    "CARD_EXPIRED_SWIPE_002",
                    "Why was transaction TXN-10928 rejected?",
                    "TXN-10928",
                    "CONFIRMED_DECLINE",
                    "expired",
                    List.of("getTransactionDetails", "getCardLedgerSummary"),
                    List.of("card-lifecycle-policy.md"),
                    "ISSUE_REPLACEMENT_CARD"
            )),
            Map.entry("CARD_BLOCKED_SECURITY_003", new BenchmarkScenario(
                    "CARD_BLOCKED_SECURITY_003",
                    "Investigate decline on card CARD-3391 for TXN-99120",
                    "TXN-99120",
                    "CONFIRMED_DECLINE",
                    "blocked",
                    List.of("getTransactionDetails", "getCardLedgerSummary"),
                    List.of("card-lifecycle-policy.md"),
                    "UNBLOCK_AFTER_KYC_OR_REISSUE"
            )),
            Map.entry("PAYMENT_GATEWAY_TIMEOUT_006", new BenchmarkScenario(
                    "PAYMENT_GATEWAY_TIMEOUT_006",
                    "Why did payment PAY-3301 encounter delays?",
                    "PAY-3301",
                    "RECOVERED_AFTER_RETRY",
                    "timeout",
                    List.of("getPaymentTimeline"),
                    List.of("authorization-policy.md"),
                    "NO_ACTION_REQUIRED"
            )),
            Map.entry("FRAUD_HIGH_VELOCITY_SPIKE_011", new BenchmarkScenario(
                    "FRAUD_HIGH_VELOCITY_SPIKE_011",
                    "Is transaction TXN-90124 suspicious?",
                    "TXN-90124",
                    "FRAUD_SUSPECTED",
                    "velocity",
                    List.of("getTransactionDetails", "getFraudRiskAssessment"),
                    List.of("velocity-rules.md"),
                    "ESCALATE_TO_FRAUD_DESK"
            )),
            Map.entry("SETTLEMENT_AMOUNT_MISMATCH_016", new BenchmarkScenario(
                    "SETTLEMENT_AMOUNT_MISMATCH_016",
                    "Why hasn't payment PAY-9321 settled correctly?",
                    "PAY-9321",
                    "SETTLEMENT_MISMATCH",
                    "mismatch",
                    List.of("getPaymentTimeline", "getSettlementDiscrepancy"),
                    List.of("capture-and-settlement.md"),
                    "ESCALATE_TO_SETTLEMENT_OPERATIONS"
            ))
    );

    public static List<BenchmarkScenario> getAllScenarios() {
        return List.copyOf(SCENARIOS.values());
    }
}

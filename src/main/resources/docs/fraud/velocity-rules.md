# Fraud Velocity & Anomaly Policy

**Policy Identifier:** `POL-FRAUD-VELOCITY-01`  
**Version:** 2.1  
**Domain:** Fraud Risk Management  

## 1. High Velocity Rules

* **5-Minute Spike Rule:** $\ge 3$ transaction authorizations in $< 5$ minutes on the same card triggers `HIGH_VELOCITY_5M` signal (+30 points).
* **Amount Deviation Rule:** Any transaction exceeding 5x the cardholder's 90-day moving average triggers `HIGH_AMOUNT_SPIKE` signal (+35 points).
* **Probing Rule:** Sequential low-value transactions (e.g. ₹50 -> ₹100) followed by a high-value swipe triggers an automated account security review.

## 2. Impossible Geographic Travel Rule

* If physical point-of-sale authorizations occur in distinct geographic cities with required flight travel velocity $> 800\text{ km/h}$, an `UNUSUAL_LOCATION_GEO` critical anomaly is generated.

## 3. Risk Thresholds & Escalation

* **Risk Score $\ge 50$:** Opens a formal `FraudCase` for manual analyst triage.
* **Risk Score $\ge 75$:** Triggers automated step-up verification / card temporary suspension.

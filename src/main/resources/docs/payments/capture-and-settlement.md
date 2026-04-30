# Capture and Settlement Reconciliation Policy

**Policy Identifier:** `POL-SETTLE-RECON-01`  
**Version:** 1.3  
**Domain:** Payment Settlement  

## 1. Settlement Batch Cycle

* Merchants submit end-of-day clearing batch files to the issuing network.
* For each payment item in the batch:
  $$\text{Expected Amount} = \text{Captured Payment Amount}$$

## 2. Discrepancy & Mismatch Protocols

* **Amount Mismatch:** When the batch clearing item reports an amount differing from the captured ledger amount without a valid registered interchange fee breakdown, the item is flagged as `SETTLEMENT_DISCREPANCY`.
* **Missing Clearing Record:** If a payment is captured but fails to settle within 48 hours, an operational investigation alert is dispatched.
* **Resolution Action:** Settlement discrepancies must be escalated to the Settlement Operations Desk for manual fee schedule audit.

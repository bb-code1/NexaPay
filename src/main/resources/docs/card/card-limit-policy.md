# Card Limit Policy & Balance Hold Rules

**Policy Identifier:** `POL-CARD-LIMIT-01`  
**Version:** 1.2  
**Domain:** Card & Ledger Operations  

## 1. Credit Limit vs Available Balance

Every approved card account maintains two balance thresholds:
1. **Approved Credit Limit:** The maximum total credit line extended to the cardholder.
2. **Available Credit Limit:** The real-time spending power remaining after subtracting posted captured balances and active authorization holds.

## 2. Authorization Hold Invariants

When a transaction authorization request is received:
* The system checks: $\text{Requested Amount} \le \text{Available Credit Limit}$.
* If true, an immediate authorization hold is placed on the available balance.
* If the requested amount exceeds available credit, the transaction **MUST BE DECLINED** with ISO-8583 response code `51` (*Insufficient Funds / Exceeds Credit Limit*).

## 3. Daily Velocity Limits

* Standard cards have a daily transaction velocity ceiling (e.g. ₹30,000 to ₹100,000).
* If cumulative daily transaction volume exceeds this ceiling, subsequent attempts are rejected with ISO-8583 response code `61` (*Exceeds Transaction Frequency / Amount Limit*).

## 4. Operator Resolution Protocol

When investigating decline code `51`:
1. Verify available credit vs attempted transaction amount.
2. Confirm active authorization holds that may be occupying available limit.
3. Advise the customer to make a repayment or submit an official credit limit expansion request.

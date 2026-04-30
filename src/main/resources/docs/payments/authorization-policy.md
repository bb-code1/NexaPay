# Payment Authorization Policy & ISO-8583 Responses

**Policy Identifier:** `POL-AUTH-RULES-01`  
**Version:** 2.0  
**Domain:** Authorization Engine  

## 1. Deterministic Rule Hierarchy

Authorization requests are processed through a strictly ordered pipeline:
1. **Card Expiry & Status Verification:** Code `54` (Expired) or `14` (Blocked/Invalid).
2. **Merchant Category Code (MCC) Screening:** Code `57` (Restricted Gambling/Crypto MCCs).
3. **Card Daily Limits:** Code `61` (Exceeds daily limits).
4. **Pessimistic Balance Verification:** Code `51` (Insufficient Funds / Limit Exceeded).

## 2. Decline Codes & Meanings

| Code | Meaning | Customer Guidance |
| :--- | :--- | :--- |
| `05` | Do Not Honor | Contact issuing bank security desk |
| `14` | Invalid / Blocked Card | Unblock via customer support or use active card |
| `51` | Insufficient Available Limit | Pay outstanding balance or raise credit limit |
| `54` | Expired Card | Activate replacement card |
| `57` | Transaction Not Permitted | Merchant category restricted by regulator |
| `61` | Exceeds Daily Limit | Retry after 24h or request temporary ceiling raise |
| `75` | PIN Tries Exceeded | Reset PIN at verified ATM or online portal |
| `91` | Issuer / Gateway Timeout | Transient network issue, retry shortly |

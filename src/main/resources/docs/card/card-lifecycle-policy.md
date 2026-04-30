# Card Lifecycle & Status Management Policy

**Policy Identifier:** `POL-CARD-LIFE-02`  
**Version:** 1.1  
**Domain:** Card Operations  

## 1. Card Status Definitions

| Status | Description | Allowed Operations |
| :--- | :--- | :--- |
| `CREATED` | Card manufactured, awaiting customer activation | Activation only |
| `ACTIVE` | Normal operational state | All authorized transactions |
| `BLOCKED` | Temporarily suspended due to suspected fraud or PIN failures | Unblock after KYC check, or close |
| `EXPIRED` | Expiration date has passed | Replacement issuance only |
| `CLOSED` | Permanently terminated account | Terminal state (no reactivation) |

## 2. Decline Code 14 (Invalid / Blocked Card)

* Any transaction attempted on a card in `BLOCKED` or `CLOSED` status is declined immediately with ISO-8583 code `14`.
* If a card was replaced with a new card credential, attempts on the old card reference will yield decline code `14`.

## 3. Decline Code 54 (Expired Card)

* Authorizations checked against current UTC calendar date.
* Swipes after the card expiration date reject with code `54`. Cardholder must activate their renewal card.

-- ============================================================================
-- 0. EXTENSIONS
-- ============================================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "vector";

-- ============================================================================
-- 1. CUSTOMER DOMAIN
-- ============================================================================
CREATE TABLE customers (
    id VARCHAR(36) PRIMARY KEY,
    customer_number VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    kyc_tier VARCHAR(20) NOT NULL CHECK (kyc_tier IN ('TIER_1_BASIC', 'TIER_2_VERIFIED', 'TIER_3_ENTERPRISE')),
    risk_category VARCHAR(20) NOT NULL CHECK (risk_category IN ('LOW', 'MEDIUM', 'HIGH')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_profiles (
    id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL UNIQUE REFERENCES customers(id) ON DELETE CASCADE,
    monthly_income NUMERIC(15, 2),
    credit_score INT CHECK (credit_score BETWEEN 300 AND 900),
    home_city VARCHAR(50),
    home_country VARCHAR(2) NOT NULL DEFAULT 'IN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 2. CARD DOMAIN
-- ============================================================================
CREATE TABLE cards (
    id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    card_number_masked VARCHAR(19) NOT NULL,
    card_number_hash VARCHAR(64) NOT NULL UNIQUE,
    card_type VARCHAR(20) NOT NULL CHECK (card_type IN ('CREDIT', 'DEBIT', 'PREPAID')),
    card_network VARCHAR(20) NOT NULL CHECK (card_network IN ('VISA', 'MASTERCARD', 'RUPAY')),
    expiration_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('CREATED', 'ACTIVE', 'BLOCKED', 'EXPIRED', 'CLOSED')),
    daily_limit NUMERIC(15, 2) NOT NULL CHECK (daily_limit >= 0),
    monthly_limit NUMERIC(15, 2) NOT NULL CHECK (monthly_limit >= daily_limit),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE card_accounts (
    id VARCHAR(36) PRIMARY KEY,
    card_id VARCHAR(36) NOT NULL UNIQUE REFERENCES cards(id) ON DELETE CASCADE,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    credit_limit NUMERIC(15, 2) NOT NULL CHECK (credit_limit >= 0),
    available_limit NUMERIC(15, 2) NOT NULL CHECK (available_limit >= 0),
    blocked_amount NUMERIC(15, 2) NOT NULL DEFAULT 0.00 CHECK (blocked_amount >= 0),
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 3. MERCHANT DOMAIN
-- ============================================================================
CREATE TABLE merchant_categories (
    code VARCHAR(4) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    risk_weight DECIMAL(3, 2) NOT NULL DEFAULT 1.00,
    is_restricted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE merchants (
    id VARCHAR(36) PRIMARY KEY,
    merchant_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    mcc_code VARCHAR(4) NOT NULL REFERENCES merchant_categories(code),
    country VARCHAR(2) NOT NULL DEFAULT 'IN',
    settlement_account_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TERMINATED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 4. TRANSACTION & AUTHORIZATION DOMAIN
-- ============================================================================
CREATE TABLE decline_codes (
    code VARCHAR(3) PRIMARY KEY,
    meaning VARCHAR(100) NOT NULL,
    category VARCHAR(30) NOT NULL CHECK (category IN ('INSUFFICIENT_FUNDS', 'CARD_STATUS', 'SECURITY_FRAUD', 'SYSTEM_ERROR')),
    customer_message TEXT NOT NULL
);

CREATE TABLE transactions (
    id VARCHAR(36) PRIMARY KEY,
    transaction_ref VARCHAR(30) NOT NULL UNIQUE,
    card_id VARCHAR(36) NOT NULL REFERENCES cards(id) ON DELETE RESTRICT,
    merchant_id VARCHAR(36) NOT NULL REFERENCES merchants(id) ON DELETE RESTRICT,
    amount NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    channel VARCHAR(20) NOT NULL CHECK (channel IN ('POS_CHIP_PIN', 'POS_CONTACTLESS', 'ECOMMERCE_3DS', 'ATM')),
    status VARCHAR(30) NOT NULL CHECK (status IN ('CREATED', 'AUTHORIZED', 'DECLINED', 'CAPTURED', 'REVERSED', 'SETTLED', 'CHARGEBACK')),
    location_city VARCHAR(50),
    location_country VARCHAR(2) NOT NULL DEFAULT 'IN',
    device_fingerprint VARCHAR(64),
    ip_address VARCHAR(45),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE authorizations (
    id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL UNIQUE REFERENCES transactions(id) ON DELETE CASCADE,
    auth_code VARCHAR(6),
    is_approved BOOLEAN NOT NULL,
    decline_code VARCHAR(3) REFERENCES decline_codes(code),
    decline_reason TEXT,
    rules_evaluated JSONB NOT NULL,
    evaluated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 5. DOUBLE-ENTRY LEDGER DOMAIN
-- ============================================================================
CREATE TABLE ledger_entries (
    id VARCHAR(36) PRIMARY KEY,
    journal_batch_id VARCHAR(36) NOT NULL,
    transaction_ref VARCHAR(30) NOT NULL,
    account_type VARCHAR(40) NOT NULL CHECK (account_type IN (
        'CUSTOMER_CARD_ACCOUNT', 'ISSUER_CLEARING', 'MERCHANT_SETTLEMENT', 
        'INTERCHANGE_FEE_REVENUE', 'DISPUTE_HOLD'
    )),
    account_id VARCHAR(36) NOT NULL,
    entry_type VARCHAR(6) NOT NULL CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    amount NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    posted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255) NOT NULL
);

-- ============================================================================
-- 6. PAYMENT, SETTLEMENT & DISPUTE DOMAIN
-- ============================================================================
CREATE TABLE payments (
    id VARCHAR(36) PRIMARY KEY,
    payment_ref VARCHAR(30) NOT NULL UNIQUE,
    transaction_id VARCHAR(36) NOT NULL REFERENCES transactions(id) ON DELETE RESTRICT,
    idempotency_key VARCHAR(64) NOT NULL UNIQUE,
    captured_amount NUMERIC(15, 2) NOT NULL CHECK (captured_amount > 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    status VARCHAR(30) NOT NULL CHECK (status IN (
        'INITIATED', 'AUTHORIZED', 'CAPTURED', 'SETTLEMENT_PENDING', 'SETTLED', 'REVERSED', 'FAILED'
    )),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    captured_at TIMESTAMPTZ,
    settled_at TIMESTAMPTZ
);

CREATE TABLE payment_attempts (
    id VARCHAR(36) PRIMARY KEY,
    payment_id VARCHAR(36) NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    attempt_number INT NOT NULL,
    gateway_response_code VARCHAR(20),
    gateway_payload JSONB,
    status VARCHAR(20) NOT NULL,
    attempted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE settlement_batches (
    id VARCHAR(36) PRIMARY KEY,
    batch_ref VARCHAR(30) NOT NULL UNIQUE,
    merchant_id VARCHAR(36) NOT NULL REFERENCES merchants(id),
    total_count INT NOT NULL CHECK (total_count >= 0),
    total_amount NUMERIC(15, 2) NOT NULL CHECK (total_amount >= 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SETTLED', 'FAILED', 'DISCREPANCY')),
    cleared_at TIMESTAMPTZ
);

CREATE TABLE settlement_items (
    id VARCHAR(36) PRIMARY KEY,
    batch_id VARCHAR(36) NOT NULL REFERENCES settlement_batches(id) ON DELETE CASCADE,
    payment_id VARCHAR(36) NOT NULL REFERENCES payments(id),
    expected_amount NUMERIC(15, 2) NOT NULL,
    settled_amount NUMERIC(15, 2) NOT NULL,
    has_mismatch BOOLEAN NOT NULL DEFAULT FALSE,
    discrepancy_reason TEXT
);

CREATE TABLE chargebacks (
    id VARCHAR(36) PRIMARY KEY,
    dispute_ref VARCHAR(30) NOT NULL UNIQUE,
    payment_id VARCHAR(36) NOT NULL REFERENCES payments(id),
    disputed_amount NUMERIC(15, 2) NOT NULL CHECK (disputed_amount > 0),
    reason_code VARCHAR(30) NOT NULL CHECK (reason_code IN ('FRAUD_UNAUTHORIZED', 'GOODS_NOT_RECEIVED', 'DUPLICATE_CHARGE')),
    status VARCHAR(30) NOT NULL CHECK (status IN ('OPENED', 'UNDER_REVIEW', 'EVIDENCE_COLLECTED', 'RESOLVED_MERCHANT_WON', 'RESOLVED_CUSTOMER_WON')),
    opened_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMPTZ
);

-- ============================================================================
-- 7. FRAUD & ANOMALY SIGNALS
-- ============================================================================
CREATE TABLE fraud_signals (
    id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    signal_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    weight INT NOT NULL,
    metadata JSONB,
    detected_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fraud_cases (
    id VARCHAR(36) PRIMARY KEY,
    case_ref VARCHAR(30) NOT NULL UNIQUE,
    card_id VARCHAR(36) NOT NULL REFERENCES cards(id),
    aggregate_risk_score INT NOT NULL CHECK (aggregate_risk_score BETWEEN 0 AND 100),
    risk_level VARCHAR(20) NOT NULL CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('OPEN', 'INVESTIGATING', 'CONFIRMED_FRAUD', 'FALSE_POSITIVE', 'CLOSED')),
    assigned_analyst_id VARCHAR(36),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 8. AUDIT & AI OBSERVABILITY
-- ============================================================================
CREATE TABLE audit_events (
    id VARCHAR(36) PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(30) NOT NULL,
    entity_id VARCHAR(50) NOT NULL,
    actor_id VARCHAR(50) NOT NULL,
    actor_role VARCHAR(30) NOT NULL,
    payload JSONB,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 9. SPRING AI PGVECTOR STORE TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS vector_store (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    content TEXT NOT NULL,
    metadata JSONB,
    embedding vector(1536)
);

-- ============================================================================
-- 10. INDEXES
-- ============================================================================
CREATE INDEX idx_transactions_card_created ON transactions(card_id, created_at DESC);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_ref ON transactions(transaction_ref);

CREATE INDEX idx_payments_ref ON payments(payment_ref);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_idempotency ON payments(idempotency_key);

CREATE INDEX idx_ledger_batch ON ledger_entries(journal_batch_id);
CREATE INDEX idx_ledger_account ON ledger_entries(account_id, posted_at DESC);

CREATE INDEX idx_fraud_signals_txn ON fraud_signals(transaction_id);
CREATE INDEX idx_fraud_cases_card ON fraud_cases(card_id, status);

CREATE INDEX idx_audit_entity_time ON audit_events(entity_type, entity_id, timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_vector_store_hnsw ON vector_store 
USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 64);

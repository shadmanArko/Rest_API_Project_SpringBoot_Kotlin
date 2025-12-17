CREATE TABLE IF NOT EXISTS analytics.accounting_events
(
    event_date   Date,
    company_id   UUID,
    account_code String,
    account_type String,
    amount       Decimal(14,2),
    source       String
)
ENGINE = MergeTree
PARTITION BY toYYYYMM(event_date)
ORDER BY (company_id, event_date, account_code);

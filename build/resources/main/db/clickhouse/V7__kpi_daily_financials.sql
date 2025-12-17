CREATE TABLE IF NOT EXISTS analytics.kpi_daily_financials
(
    event_date   Date,
    company_id   UUID,
    account_type String,        -- EXPENSE / INCOME
    total_amount Decimal(14,2)
)
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(event_date)
ORDER BY (company_id, event_date, account_type);

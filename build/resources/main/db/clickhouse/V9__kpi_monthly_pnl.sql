CREATE TABLE IF NOT EXISTS analytics.kpi_monthly_pnl
(
    month        Date,
    company_id   UUID,
    income       Decimal(14,2),
    expense      Decimal(14,2)
)
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(month)
ORDER BY (company_id, month);

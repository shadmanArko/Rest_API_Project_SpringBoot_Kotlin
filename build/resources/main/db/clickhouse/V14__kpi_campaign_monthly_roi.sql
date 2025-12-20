CREATE TABLE IF NOT EXISTS analytics.kpi_campaign_monthly_roi
(
    month       Date,
    company_id  UUID,
    campaign_id UUID,
    cost        Decimal(14,2),
    revenue     Decimal(14,2)
)
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(month)
ORDER BY (company_id, campaign_id, month);

CREATE TABLE IF NOT EXISTS analytics.kpi_campaign_daily
(
    event_date  Date,
    company_id  UUID,
    campaign_id UUID,
    cost        Decimal(14,2),
    revenue     Decimal(14,2)
)
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(event_date)
ORDER BY (company_id, campaign_id, event_date);

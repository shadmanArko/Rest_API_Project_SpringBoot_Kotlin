CREATE TABLE IF NOT EXISTS analytics.campaign_events
(
    event_date   Date,
    company_id   UUID,
    campaign_id  UUID,
    event_type   String,        -- COST / REVENUE
    amount       Decimal(14,2)
)
ENGINE = MergeTree
PARTITION BY toYYYYMM(event_date)
ORDER BY (company_id, campaign_id, event_date);

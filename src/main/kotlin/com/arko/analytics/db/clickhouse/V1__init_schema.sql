CREATE DATABASE IF NOT EXISTS analytics;

CREATE TABLE IF NOT EXISTS analytics.raw_events
(
    event_time     DateTime,
    company_id     UUID,
    event_type     String,
    campaign_id    UUID,
    user_id        UUID,
    spend          Decimal(12,2),
    meta           String
)
ENGINE = MergeTree
PARTITION BY toYYYYMM(event_time)
ORDER BY (company_id, event_time);

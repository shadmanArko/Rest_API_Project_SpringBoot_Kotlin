CREATE TABLE IF NOT EXISTS analytics.agg_campaign_daily
(
    date Date,
    platform String,
    account_id Nullable(String),
    campaign_id Nullable(String),

    impressions UInt64,
    clicks UInt64,
    spend Float64,
    orders UInt64,
    revenue Float64
)
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(date)
ORDER BY (date, platform, account_id, campaign_id);

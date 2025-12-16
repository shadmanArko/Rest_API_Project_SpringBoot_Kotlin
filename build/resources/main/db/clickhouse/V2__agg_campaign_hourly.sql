CREATE DATABASE IF NOT EXISTS analytics;

CREATE TABLE IF NOT EXISTS analytics.agg_campaign_hourly (
  date_hour DateTime,
  platform String,
  account_id String,
  campaign_id String,
  impressions UInt64,
  clicks UInt64,
  spend Float64,
  orders UInt64,
  revenue Float64
) ENGINE = SummingMergeTree(date_hour, (platform, account_id, campaign_id), 8192);

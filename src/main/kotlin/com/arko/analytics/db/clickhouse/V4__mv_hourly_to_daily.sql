CREATE MATERIALIZED VIEW IF NOT EXISTS analytics.mv_hourly_to_daily
TO analytics.agg_campaign_daily
AS
SELECT
    toDate(date_hour) AS date,
    platform,
    account_id,
    campaign_id,

    sum(impressions) AS impressions,
    sum(clicks) AS clicks,
    sum(spend) AS spend,
    sum(orders) AS orders,
    sum(revenue) AS revenue
FROM analytics.agg_campaign_hourly
GROUP BY
    date,
    platform,
    account_id,
    campaign_id;

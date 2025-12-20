CREATE MATERIALIZED VIEW IF NOT EXISTS analytics.mv_campaign_monthly_roi
TO analytics.kpi_campaign_monthly_roi
AS
SELECT
    toStartOfMonth(event_date) AS month,
    company_id,
    campaign_id,
    sum(cost)    AS cost,
    sum(revenue) AS revenue
FROM analytics.kpi_campaign_daily
GROUP BY
    month,
    company_id,
    campaign_id;

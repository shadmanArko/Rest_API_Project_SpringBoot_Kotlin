CREATE MATERIALIZED VIEW IF NOT EXISTS analytics.mv_campaign_daily
TO analytics.kpi_campaign_daily
AS
SELECT
    event_date,
    company_id,
    campaign_id,
    sumIf(amount, event_type = 'COST')     AS cost,
    sumIf(amount, event_type = 'REVENUE')  AS revenue
FROM analytics.campaign_events
GROUP BY
    event_date,
    company_id,
    campaign_id;

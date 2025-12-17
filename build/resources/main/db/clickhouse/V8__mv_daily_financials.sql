CREATE MATERIALIZED VIEW IF NOT EXISTS analytics.mv_daily_financials
TO analytics.kpi_daily_financials
AS
SELECT
    event_date,
    company_id,
    account_type,
    sum(amount) AS total_amount
FROM analytics.accounting_events
GROUP BY
    event_date,
    company_id,
    account_type;

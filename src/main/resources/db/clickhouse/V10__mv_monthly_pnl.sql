CREATE MATERIALIZED VIEW IF NOT EXISTS analytics.mv_monthly_pnl
TO analytics.kpi_monthly_pnl
AS
SELECT
    toStartOfMonth(event_date) AS month,
    company_id,
    sumIf(total_amount, account_type = 'INCOME')  AS income,
    sumIf(total_amount, account_type = 'EXPENSE') AS expense
FROM analytics.kpi_daily_financials
GROUP BY
    month,
    company_id;

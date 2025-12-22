package com.arko.analytics.service.impl

import com.arko.analytics.dto.CampaignMetricsDto
import com.arko.analytics.dto.CampaignOverviewDto
import com.arko.analytics.dto.DailyFinancialDto
import com.arko.analytics.dto.MonthlyPnLDto
import com.arko.analytics.dto.OverviewDto
import com.arko.analytics.dto.TimePointDto
import com.arko.analytics.service.AnalyticsQueryService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.sql.DataSource

@Service("clickHouseQueryService")
@ConditionalOnProperty(
    name = ["app.clickhouse.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class ClickHouseAnalyticsQueryService(
    @org.springframework.beans.factory.annotation.Qualifier("clickHouseDataSource")
    private val dataSource: DataSource
) : AnalyticsQueryService {

    // ------------------ OVERVIEW ------------------

    override fun getOverview(
        from: LocalDateTime,
        to: LocalDateTime,
        platform: String?
    ): OverviewDto {

        val sql = StringBuilder("""
            SELECT
              sum(spend) as total_spend,
              sum(revenue) as total_revenue,
              sum(orders) as total_orders,
              sum(impressions) as total_impressions
            FROM analytics.agg_campaign_hourly
            WHERE date_hour >= ? AND date_hour < ?
        """.trimIndent())

        if (!platform.isNullOrBlank()) {
            sql.append(" AND platform = ?")
        }

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql.toString()).use { ps ->
                ps.setObject(1, from)
                ps.setObject(2, to)
                if (!platform.isNullOrBlank()) {
                    ps.setString(3, platform)
                }

                ps.executeQuery().use { rs ->
                    if (!rs.next()) {
                        return OverviewDto.zero()
                    }

                    val spend = rs.getBigDecimal("total_spend") ?: BigDecimal.ZERO
                    val revenue = rs.getBigDecimal("total_revenue") ?: BigDecimal.ZERO
                    val orders = rs.getLong("total_orders")
                    val impressions = rs.getLong("total_impressions")

                    val roas =
                        if (spend.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO
                        else revenue.divide(spend, 4, BigDecimal.ROUND_HALF_UP)

                    val avgCpa =
                        if (orders == 0L) BigDecimal.ZERO
                        else spend.divide(BigDecimal.valueOf(orders), 4, BigDecimal.ROUND_HALF_UP)

                    return OverviewDto(spend, revenue, roas, orders, impressions, avgCpa)
                }
            }
        }
    }

    // ------------------ CAMPAIGN METRICS ------------------

    override fun getCampaignMetrics(
        campaignId: String,
        from: LocalDateTime,
        to: LocalDateTime,
        granularity: String
    ): CampaignMetricsDto {

        val groupExpr =
            if (granularity == "day") "toStartOfDay(date_hour)"
            else "date_hour"

        val sql = """
            SELECT
                $groupExpr AS ts,
                sum(impressions) AS impressions,
                sum(clicks) AS clicks,
                sum(spend) AS spend,
                sum(orders) AS orders,
                sum(revenue) AS revenue
            FROM analytics.agg_campaign_hourly
            WHERE campaign_id = ?
              AND date_hour >= ?
              AND date_hour < ?
            GROUP BY ts
            ORDER BY ts
        """.trimIndent()

        val series = mutableListOf<TimePointDto>()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, campaignId)
                ps.setObject(2, from)
                ps.setObject(3, to)

                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        val ts = when (val obj = rs.getObject("ts")) {
                            is Timestamp -> obj.toLocalDateTime()
                            is LocalDateTime -> obj
                            else -> LocalDateTime.ofEpochSecond(
                                rs.getLong("ts") / 1000,
                                0,
                                ZoneOffset.UTC
                            )
                        }

                        series.add(
                            TimePointDto(
                                timestamp = ts,
                                impressions = rs.getLong("impressions"),
                                clicks = rs.getLong("clicks"),
                                spend = rs.getBigDecimal("spend") ?: BigDecimal.ZERO,
                                orders = rs.getLong("orders"),
                                revenue = rs.getBigDecimal("revenue") ?: BigDecimal.ZERO
                            )
                        )
                    }
                }
            }
        }

        return CampaignMetricsDto(campaignId, series)
    }

    override fun getCampaignOverview(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<CampaignOverviewDto> {

        val sql = """
        SELECT
            campaign_id,
            sum(cost) AS cost,
            sum(revenue) AS revenue
        FROM analytics.kpi_campaign_daily
        WHERE event_date >= toDate(?)
          AND event_date < toDate(?)
        GROUP BY campaign_id
        ORDER BY revenue DESC
    """.trimIndent()

        val result = mutableListOf<CampaignOverviewDto>()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setObject(1, from)
                ps.setObject(2, to)

                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        val cost = rs.getBigDecimal("cost") ?: BigDecimal.ZERO
                        val revenue = rs.getBigDecimal("revenue") ?: BigDecimal.ZERO

                        result.add(
                            CampaignOverviewDto(
                                campaignId = rs.getString("campaign_id"),
                                cost = cost,
                                revenue = revenue,
                                profit = revenue.subtract(cost),
                                roas = if (cost.signum() == 0) BigDecimal.ZERO
                                else revenue.divide(cost, 4, BigDecimal.ROUND_HALF_UP)
                            )
                        )
                    }
                }
            }
        }

        return result
    }


    override fun getDailyFinancials(
        from: LocalDateTime,
        to: LocalDateTime,
        companyId: String?
    ): List<DailyFinancialDto> {

        val sql = StringBuilder("""
        SELECT
            event_date,
            account_type,
            sum(total_amount) AS amount
        FROM analytics.kpi_daily_financials
        WHERE event_date >= toDate(?)
          AND event_date < toDate(?)
    """.trimIndent())

        if (!companyId.isNullOrBlank()) {
            sql.append(" AND company_id = ?")
        }

        sql.append(" GROUP BY event_date, account_type ORDER BY event_date")

        val result = mutableListOf<DailyFinancialDto>()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql.toString()).use { ps ->
                ps.setObject(1, from)
                ps.setObject(2, to)
                if (!companyId.isNullOrBlank()) {
                    ps.setString(3, companyId)
                }

                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        result.add(
                            DailyFinancialDto(
                                date = rs.getDate("event_date").toLocalDate(),
                                accountType = rs.getString("account_type"),
                                amount = rs.getBigDecimal("amount") ?: BigDecimal.ZERO
                            )
                        )
                    }
                }
            }
        }

        return result
    }


    override fun getMonthlyPnL(
        from: LocalDateTime,
        to: LocalDateTime,
        companyId: String?
    ): List<MonthlyPnLDto> {

        val sql = StringBuilder("""
        SELECT
            month,
            income,
            expense
        FROM analytics.kpi_monthly_pnl
        WHERE month >= toDate(?)
          AND month < toDate(?)
    """.trimIndent())

        if (!companyId.isNullOrBlank()) {
            sql.append(" AND company_id = ?")
        }

        sql.append(" ORDER BY month")

        val result = mutableListOf<MonthlyPnLDto>()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql.toString()).use { ps ->
                ps.setObject(1, from)
                ps.setObject(2, to)
                if (!companyId.isNullOrBlank()) {
                    ps.setString(3, companyId)
                }

                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        val income = rs.getBigDecimal("income") ?: BigDecimal.ZERO
                        val expense = rs.getBigDecimal("expense") ?: BigDecimal.ZERO

                        result.add(
                            MonthlyPnLDto(
                                month = rs.getDate("month").toLocalDate(),
                                income = income,
                                expense = expense,
                                profit = income.subtract(expense)
                            )
                        )
                    }
                }
            }
        }

        return result
    }

}

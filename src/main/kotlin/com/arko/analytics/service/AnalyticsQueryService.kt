package com.arko.analytics.service

import com.arko.analytics.dto.CampaignMetricsDto
import com.arko.analytics.dto.OverviewDto
import com.arko.analytics.dto.TimePointDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.sql.Connection
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
@ConditionalOnProperty(name = ["app.clickhouse.enabled"], havingValue = "true", matchIfMissing = false)
class AnalyticsQueryService(
    @org.springframework.beans.factory.annotation.Qualifier("clickHouseDataSource")
    private val dataSource: javax.sql.DataSource
) {

    /**
     * Overview KPIs across the time window and optional platform filter.
     */
    fun getOverview(from: LocalDateTime, to: LocalDateTime, platform: String? = null): OverviewDto {
        val sql = StringBuilder("""
            SELECT
              sum(spend) as total_spend,
              sum(revenue) as total_revenue,
              sum(orders) as total_orders
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
                if (!platform.isNullOrBlank()) ps.setString(3, platform)

                ps.executeQuery().use { rs ->
                    if (!rs.next()) {
                        return OverviewDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L, BigDecimal.ZERO)
                    }
                    val spend = rs.getDouble("total_spend").let { BigDecimal.valueOf(it) }
                    val revenue = rs.getDouble("total_revenue").let { BigDecimal.valueOf(it) }
                    val orders = rs.getLong("total_orders")

                    val roas = if (spend.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO
                    else revenue.divide(spend, 4, BigDecimal.ROUND_HALF_UP)

                    val avgCpa = if (orders == 0L) BigDecimal.ZERO
                    else spend.divide(BigDecimal.valueOf(orders), 4, BigDecimal.ROUND_HALF_UP)

                    return OverviewDto(spend, revenue, roas, orders, avgCpa)
                }
            }
        }
    }

    /**
     * Campaign metrics time series at given granularity ("hour" or "day")
     */
    fun getCampaignMetrics(campaignId: String, from: LocalDateTime, to: LocalDateTime, granularity: String = "hour"): CampaignMetricsDto {
        // choose grouping expression
        val groupExpr = when (granularity) {
            "day" -> "toStartOfDay(date_hour)"
            else -> "date_hour" // hour
        }

        val sql = """
            SELECT $groupExpr as ts,
                   sum(impressions) as impressions,
                   sum(clicks) as clicks,
                   sum(spend) as spend,
                   sum(orders) as orders,
                   sum(revenue) as revenue
            FROM analytics.agg_campaign_hourly
            WHERE campaign_id = ? AND date_hour >= ? AND date_hour < ?
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
                        val tsObj = rs.getObject("ts")
                        val timestamp = when (tsObj) {
                            is java.sql.Timestamp -> tsObj.toLocalDateTime()
                            is java.time.LocalDateTime -> tsObj
                            else -> LocalDateTime.ofEpochSecond(rs.getLong("ts") / 1000, 0, ZoneOffset.UTC)
                        }
                        val impressions = rs.getLong("impressions")
                        val clicks = rs.getLong("clicks")
                        val spend = BigDecimal.valueOf(rs.getDouble("spend"))
                        val orders = rs.getLong("orders")
                        val revenue = BigDecimal.valueOf(rs.getDouble("revenue"))

                        series.add(TimePointDto(timestamp, impressions, clicks, spend, orders, revenue))
                    }
                }
            }
        }

        return CampaignMetricsDto(campaignId, series)
    }
}

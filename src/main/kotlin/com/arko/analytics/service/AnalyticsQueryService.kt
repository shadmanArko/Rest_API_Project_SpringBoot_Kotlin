package com.arko.analytics.service

import com.arko.analytics.dto.*
import java.time.LocalDate
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.sql.Connection
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.Instant
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZoneId

@Service
class AnalyticsQueryService(
    @Autowired(required = false)
    @Qualifier("clickHouseDataSource")
    private val clickHouseDataSource: DataSource?,

    @Qualifier("dataSource")
    private val postgresDataSource: DataSource
) {

    /**
     * Helper to run query with safe fallback
     */
    private fun <T> runQuery(
        clickHouseSql: String,
        params: List<Any>,
        mapper: (java.sql.ResultSet) -> T,
        fallback: () -> T
    ): T {
        // 1. Try ClickHouse
        if (clickHouseDataSource != null) {
            try {
                clickHouseDataSource.connection.use { conn ->
                    conn.prepareStatement(clickHouseSql).use { ps ->
                        params.forEachIndexed { i, p -> ps.setObject(i + 1, p) }
                        ps.executeQuery().use { rs -> return mapper(rs) }
                    }
                }
            } catch (e: Exception) {
                println("ClickHouse query failed: ${e.message}")
            }
        }

        // 2. Try Postgres Fallback
        return try {
            fallback()
        } catch (e: Exception) {
            println("Postgres fallback failed: ${e.message}")
            throw e // Let the caller handle ultimate failure or return default
        }
    }

    private fun Instant.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(this, ZoneOffset.UTC)

    fun getOverview(from: Instant, to: Instant, platform: String? = null): OverviewDto {
        val clickHouseSql = StringBuilder("""
            SELECT
              sum(spend) as total_spend,
              sum(revenue) as total_revenue,
              sum(orders) as total_orders
            FROM analytics.agg_campaign_hourly
            WHERE date_hour >= ? AND date_hour < ?
        """.trimIndent())
        if (!platform.isNullOrBlank()) clickHouseSql.append(" AND platform = ?")

        return try {
            runQuery(
                clickHouseSql.toString(),
                listOfNotNull(from.toLocalDateTime(), to.toLocalDateTime(), platform),
                { rs ->
                    if (!rs.next()) OverviewDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L, BigDecimal.ZERO)
                    else {
                        val spend = BigDecimal.valueOf(rs.getDouble("total_spend"))
                        val revenue = BigDecimal.valueOf(rs.getDouble("total_revenue"))
                        val orders = rs.getLong("total_orders")
                        val roas = if (spend.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else revenue.divide(spend, 4, BigDecimal.ROUND_HALF_UP)
                        val avgCpa = if (orders == 0L) BigDecimal.ZERO else spend.divide(BigDecimal.valueOf(orders), 4, BigDecimal.ROUND_HALF_UP)
                        OverviewDto(spend, revenue, roas, orders, avgCpa)
                    }
                },
                { getOverviewPostgres(from, to, platform) }
            )
        } catch (e: Exception) {
            OverviewDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L, BigDecimal.ZERO)
        }
    }

    private fun getOverviewPostgres(from: Instant, to: Instant, platform: String?): OverviewDto {
        val sql = StringBuilder("""
            SELECT 
                COUNT(*) FILTER (WHERE event_type = 'impression') as impressions,
                COUNT(*) FILTER (WHERE event_type = 'order') as orders,
                SUM(CASE WHEN event_type = 'order' THEN amount ELSE 0 END) as revenue
            FROM raw_events
            WHERE event_time >= ? AND event_time < ?
        """.trimIndent())
        if (!platform.isNullOrBlank()) sql.append(" AND platform = ?")

        return postgresDataSource.connection.use { conn ->
            conn.prepareStatement(sql.toString()).use { ps ->
                ps.setObject(1, from)
                ps.setObject(2, to)
                if (!platform.isNullOrBlank()) ps.setString(3, platform)
                
                ps.executeQuery().use { rs ->
                    if (!rs.next()) OverviewDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L, BigDecimal.ZERO)
                    else {
                        val revenue = BigDecimal.valueOf(rs.getDouble("revenue"))
                        val orders = rs.getLong("orders")
                        val impressions = rs.getLong("impressions")
                        val spend = BigDecimal.valueOf(impressions * 0.15) // CPM mock
                        val roas = if (spend.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else revenue.divide(spend, 4, BigDecimal.ROUND_HALF_UP)
                        val avgCpa = if (orders == 0L) BigDecimal.ZERO else spend.divide(BigDecimal.valueOf(orders), 4, BigDecimal.ROUND_HALF_UP)
                        OverviewDto(spend, revenue, roas, orders, avgCpa)
                    }
                }
            }
        }
    }

    fun getCampaignMetrics(campaignId: String, from: Instant, to: Instant, granularity: String = "hour"): CampaignMetricsDto {
        try {
            val clickHouseSql = """
                SELECT toStartOfDay(date_hour) as ts,
                       sum(impressions) as impressions,
                       sum(clicks) as clicks,
                       sum(spend) as spend,
                       sum(orders) as orders,
                       sum(revenue) as revenue
                FROM analytics.agg_campaign_hourly
                WHERE campaign_id = ? AND date_hour >= ? AND date_hour < ?
                GROUP BY ts ORDER BY ts
            """.trimIndent()
            
            return runQuery(clickHouseSql, listOf(campaignId, from.toLocalDateTime(), to.toLocalDateTime()), { rs ->
                val series = mutableListOf<TimePointDto>()
                while (rs.next()) {
                    series.add(TimePointDto(
                        rs.getObject("ts", LocalDateTime::class.java),
                        rs.getLong("impressions"),
                        rs.getLong("clicks"),
                        BigDecimal.valueOf(rs.getDouble("spend")),
                        rs.getLong("orders"),
                        BigDecimal.valueOf(rs.getDouble("revenue"))
                    ))
                }
                CampaignMetricsDto(campaignId, series)
            }, { getCampaignMetricsPostgres(campaignId, from, to) })
        } catch (e: Exception) {
            return CampaignMetricsDto(campaignId, emptyList())
        }
    }

    private fun getCampaignMetricsPostgres(campaignId: String, from: Instant, to: Instant): CampaignMetricsDto {
        val sql = """
            SELECT 
                DATE(event_time) as d,
                COUNT(*) FILTER (WHERE event_type = 'impression') as impressions,
                COUNT(*) FILTER (WHERE event_type = 'click') as clicks,
                COUNT(*) FILTER (WHERE event_type = 'order') as orders,
                SUM(CASE WHEN event_type = 'order' THEN amount ELSE 0 END) as revenue
            FROM raw_events
            WHERE campaign_id = ? AND event_time >= ? AND event_time < ?
            GROUP BY d ORDER BY d
        """.trimIndent()
        
        val series = mutableListOf<TimePointDto>()
        postgresDataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, campaignId)
                ps.setObject(2, from)
                ps.setObject(3, to)
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        val revenue = BigDecimal.valueOf(rs.getDouble("revenue"))
                        val impressions = rs.getLong("impressions")
                        val spend = BigDecimal.valueOf(impressions * 0.15)
                        series.add(TimePointDto(
                            rs.getDate("d").toLocalDate().atStartOfDay(),
                            impressions,
                            rs.getLong("clicks"),
                            spend,
                            rs.getLong("orders"),
                            revenue
                        ))
                    }
                }
            }
        }
        return CampaignMetricsDto(campaignId, series)
    }

    fun getAllCampaigns(from: Instant, to: Instant): List<CampaignSummaryDto> {
        val clickHouseSql = """
            SELECT campaign_id, platform as source,
                   sum(impressions) as impressions,
                   sum(clicks) as clicks,
                   sum(spend) as spend,
                   sum(revenue) as revenue,
                   sum(orders) as orders
            FROM analytics.agg_campaign_hourly
            WHERE date_hour >= ? AND date_hour < ?
            GROUP BY campaign_id, source
        """.trimIndent()

        return try {
            runQuery(clickHouseSql, listOf(from.toLocalDateTime(), to.toLocalDateTime()), { rs ->
                val results = mutableListOf<CampaignSummaryDto>()
                while (rs.next()) {
                    val spend = BigDecimal.valueOf(rs.getDouble("spend"))
                    val revenue = BigDecimal.valueOf(rs.getDouble("revenue"))
                    val roas = if (spend.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else revenue.divide(spend, 4, BigDecimal.ROUND_HALF_UP)
                    results.add(CampaignSummaryDto(rs.getString("campaign_id"), rs.getString("source"), rs.getLong("impressions"), rs.getLong("clicks"), spend, revenue, rs.getLong("orders"), roas))
                }
                results
            }, { getAllCampaignsPostgres(from, to) })
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getAllCampaignsPostgres(from: Instant, to: Instant): List<CampaignSummaryDto> {
        val sql = """
            SELECT 
                campaign_id, platform as source,
                COUNT(*) FILTER (WHERE event_type = 'impression') as impressions,
                COUNT(*) FILTER (WHERE event_type = 'click') as clicks,
                COUNT(*) FILTER (WHERE event_type = 'order') as orders,
                SUM(CASE WHEN event_type = 'order' THEN amount ELSE 0 END) as revenue
            FROM raw_events
            WHERE event_time >= ? AND event_time < ?
            GROUP BY campaign_id, source
        """.trimIndent()

        val results = mutableListOf<CampaignSummaryDto>()
        postgresDataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setObject(1, from)
                ps.setObject(2, to)
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        val revenue = BigDecimal.valueOf(rs.getDouble("revenue"))
                        val impressions = rs.getLong("impressions")
                        val spend = BigDecimal.valueOf(impressions * 0.15)
                        val roas = if (spend.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else revenue.divide(spend, 4, BigDecimal.ROUND_HALF_UP)
                        results.add(CampaignSummaryDto(
                            rs.getString("campaign_id") ?: "UNKNOWN",
                            rs.getString("source") ?: "Direct",
                            impressions,
                            rs.getLong("clicks"),
                            spend,
                            revenue,
                            rs.getLong("orders"),
                            roas
                        ))
                    }
                }
            }
        }
        return results
    }

    fun getFinancials(from: Instant, to: Instant, interval: String = "day"): List<FinancialsDto> {
        val metrics = getCampaignMetrics("ALL", from, to, interval)
        return metrics.series.map { 
            val profit = it.revenue.subtract(it.spend)
            val roas = if (it.spend.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else it.revenue.divide(it.spend, 4, BigDecimal.ROUND_HALF_UP)
            FinancialsDto(it.timestamp.toLocalDate(), it.revenue, it.spend, profit, roas)
        }
    }

    fun getPlatformMetrics(from: Instant, to: Instant): List<PlatformMetricDto> {
        val campaigns = getAllCampaigns(from, to)
        val platformMap = campaigns.groupBy { it.source }
        val totalSpend = campaigns.sumOf { it.spend }
        
        return platformMap.map { (source, camps) ->
            val spend = camps.sumOf { it.spend }
            val revenue = camps.sumOf { it.revenue }
            val share = if (totalSpend.compareTo(BigDecimal.ZERO) == 0) 0.0 else spend.divide(totalSpend, 4, BigDecimal.ROUND_HALF_UP).toDouble()
            PlatformMetricDto(source, camps.sumOf { it.impressions }, camps.sumOf { it.clicks }, spend, revenue, share)
        }
    }

    fun getPlatformTrend(platform: String, from: Instant, to: Instant): PlatformTrendDto {
        val metrics = getCampaignMetrics(platform, from, to, "day")
        return PlatformTrendDto(platform, metrics.series)
    }
}

package com.arko.analytics.service

import com.arko.analytics.dto.CampaignMetric
import com.arko.analytics.dto.DashboardOverview
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class DashboardService(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getOverview(): DashboardOverview {
        val sql = """
            SELECT 
                countIf(event_type = 'impression') as impressions,
                countIf(event_type = 'click') as clicks,
                sumIf(amount, event_type = 'spend' AND amount IS NOT NULL) as spend,
                sumIf(amount, event_type = 'order' AND amount IS NOT NULL) as revenue
            FROM normalized_events
        """.trimIndent()

        return jdbcTemplate.queryForObject(sql) { rs, _ ->
            val impressions = rs.getLong("impressions")
            val clicks = rs.getLong("clicks")
            val spend = rs.getBigDecimal("spend") ?: BigDecimal.ZERO
            val revenue = rs.getBigDecimal("revenue") ?: BigDecimal.ZERO

            val roas = if (spend.compareTo(BigDecimal.ZERO) > 0) 
                revenue.divide(spend, 2, RoundingMode.HALF_UP) else BigDecimal.ZERO
            
            val cpa = if (clicks > 0) 
                spend.divide(BigDecimal(clicks), 2, RoundingMode.HALF_UP) else BigDecimal.ZERO

            DashboardOverview(impressions, clicks, spend, revenue, roas, cpa)
        }!!
    }

    fun getCampaignMetrics(): List<CampaignMetric> {
        val sql = """
            SELECT 
                campaign_id,
                source,
                countIf(event_type = 'impression') as impressions,
                countIf(event_type = 'click') as clicks,
                sumIf(amount, event_type = 'spend' AND amount IS NOT NULL) as spend,
                sumIf(amount, event_type = 'order' AND amount IS NOT NULL) as revenue
            FROM normalized_events
            WHERE campaign_id IS NOT NULL
            GROUP BY campaign_id, source
            ORDER BY spend DESC
        """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->
            CampaignMetric(
                campaignId = rs.getString("campaign_id"),
                source = rs.getString("source"),
                impressions = rs.getLong("impressions"),
                clicks = rs.getLong("clicks"),
                spend = rs.getBigDecimal("spend") ?: BigDecimal.ZERO,
                revenue = rs.getBigDecimal("revenue") ?: BigDecimal.ZERO
            )
        }
    }
}

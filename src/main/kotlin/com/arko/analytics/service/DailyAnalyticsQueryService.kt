package com.arko.analytics.service

import com.arko.analytics.dto.TimePointDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.sql.Connection
import java.time.LocalDate

@Service
@ConditionalOnProperty(name = ["app.clickhouse.enabled"], havingValue = "true", matchIfMissing = false)
class DailyAnalyticsQueryService(
    private val clickhouseConnection: Connection
) {

    fun campaignDailyMetrics(
        campaignId: String,
        from: LocalDate,
        to: LocalDate
    ): List<TimePointDto> {

        val sql = """
            SELECT
                date,
                sum(impressions) AS impressions,
                sum(clicks) AS clicks,
                sum(spend) AS spend,
                sum(orders) AS orders,
                sum(revenue) AS revenue
            FROM analytics.agg_campaign_daily
            WHERE campaign_id = ?
              AND date >= ?
              AND date < ?
            GROUP BY date
            ORDER BY date
        """.trimIndent()

        val result = mutableListOf<TimePointDto>()

        clickhouseConnection.prepareStatement(sql).use { ps ->
            ps.setString(1, campaignId)
            ps.setObject(2, from)
            ps.setObject(3, to)

            ps.executeQuery().use { rs ->
                while (rs.next()) {
                    result.add(
                        TimePointDto(
                            timestamp = rs.getDate("date").toLocalDate().atStartOfDay(),
                            impressions = rs.getLong("impressions"),
                            clicks = rs.getLong("clicks"),
                            spend = BigDecimal.valueOf(rs.getDouble("spend")),
                            orders = rs.getLong("orders"),
                            revenue = BigDecimal.valueOf(rs.getDouble("revenue"))
                        )
                    )
                }
            }
        }
        return result
    }
}

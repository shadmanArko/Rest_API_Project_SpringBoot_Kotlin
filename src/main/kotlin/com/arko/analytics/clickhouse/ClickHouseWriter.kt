package com.arko.analytics.clickhouse

import com.arko.analytics.aggregation.AggregatedMetrics
import com.arko.analytics.aggregation.CampaignHourKey
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.sql.Connection

@Component
@ConditionalOnProperty(name = ["app.clickhouse.enabled"], havingValue = "true", matchIfMissing = false)
class ClickHouseWriter(
    @org.springframework.beans.factory.annotation.Qualifier("clickHouseDataSource")
    private val dataSource: javax.sql.DataSource
) {

    fun write(key: CampaignHourKey, metrics: AggregatedMetrics) {
        val sql = """
            INSERT INTO analytics.agg_campaign_hourly
            (date_hour, platform, account_id, campaign_id, impressions, clicks, spend, orders, revenue)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setObject(1, key.dateHour)
                ps.setString(2, key.platform)
                ps.setString(3, key.accountId)
                ps.setString(4, key.campaignId)
                ps.setLong(5, metrics.impressions)
                ps.setLong(6, metrics.clicks)
                ps.setDouble(7, metrics.spend)
                ps.setLong(8, metrics.orders)
                ps.setDouble(9, metrics.revenue)
                ps.executeUpdate()
            }
        }
    }
}

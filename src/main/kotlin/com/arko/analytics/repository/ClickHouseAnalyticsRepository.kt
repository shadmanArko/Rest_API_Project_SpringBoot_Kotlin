package com.arko.analytics.repository

import com.arko.analytics.dto.CampaignKpiResponse
import com.arko.analytics.dto.FinancialKpiResponse
import com.clickhouse.jdbc.ClickHouseDataSource
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
class ClickHouseAnalyticsRepository(private val dataSource: ClickHouseDataSource) {

    fun getDailyFinancialKpis(): List<FinancialKpiResponse> {
        val sql = """
            SELECT
                toDate(event_date) AS period,
                sumIf(amount, account_type='REVENUE') AS totalRevenue,
                sumIf(amount, account_type='EXPENSE') AS totalExpense
            FROM analytics.accounting_events
            GROUP BY period
            ORDER BY period DESC
        """.trimIndent()

        val result = mutableListOf<FinancialKpiResponse>()
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                while (rs.next()) {
                    val revenue = rs.getBigDecimal("totalRevenue") ?: BigDecimal.ZERO
                    val expense = rs.getBigDecimal("totalExpense") ?: BigDecimal.ZERO
                    result.add(
                        FinancialKpiResponse(
                            period = rs.getString("period"),
                            totalRevenue = revenue,
                            totalExpense = expense,
                            grossProfit = revenue - expense,
                            netProfit = revenue - expense
                        )
                    )
                }
            }
        }
        return result
    }

    fun getCampaignKpis(): List<CampaignKpiResponse> {
        val sql = """
            SELECT
                campaign_id,
                sum(amount) AS totalCost
            FROM analytics.campaign_events
            WHERE event_type='COST'
            GROUP BY campaign_id
        """.trimIndent()

        val result = mutableListOf<CampaignKpiResponse>()
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                while (rs.next()) {
                    result.add(
                        CampaignKpiResponse(
                            campaignId = UUID.fromString(rs.getString("campaign_id")),
                            totalCost = rs.getBigDecimal("totalCost") ?: BigDecimal.ZERO,
                            totalRevenue = BigDecimal.ZERO, // join with revenue if available
                            roi = BigDecimal.ZERO
                        )
                    )
                }
            }
        }
        return result
    }
}

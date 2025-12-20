package com.arko.analytics.service

import com.arko.accounting.analytics.events.CampaignEvent
import org.springframework.stereotype.Service
import javax.sql.DataSource

@Service
class CampaignAnalyticsIngestService(
    private val clickHouseDataSource: DataSource
) {

    fun ingest(event: CampaignEvent) {
        clickHouseDataSource.connection.use { conn ->
            conn.prepareStatement(
                """
                INSERT INTO analytics.campaign_events
                (event_date, company_id, campaign_id, event_type, amount)
                VALUES (?, ?, ?, ?, ?)
                """
            ).apply {
                setDate(1, java.sql.Date.valueOf(event.eventDate))
                setObject(2, event.companyId)
                setObject(3, event.campaignId)
                setString(4, event.eventType)
                setBigDecimal(5, event.amount)
                execute()
            }
        }
    }
}
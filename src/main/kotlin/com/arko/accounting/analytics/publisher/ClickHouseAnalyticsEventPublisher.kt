package com.arko.accounting.analytics.publisher

import com.arko.accounting.analytics.events.AccountingEvent
import com.arko.accounting.analytics.events.CampaignEvent
import com.arko.analytics.service.AccountingAnalyticsIngestService
import com.arko.analytics.service.CampaignAnalyticsIngestService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["app.clickhouse.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class ClickHouseAnalyticsEventPublisher(
    private val accountingIngestService: AccountingAnalyticsIngestService,
    private val campaignIngestService: CampaignAnalyticsIngestService
) : AnalyticsEventPublisher {

    override fun publish(event: AccountingEvent) {
        accountingIngestService.ingest(event)
    }

    override fun publishCampaignEvent(event: CampaignEvent) {
        campaignIngestService.ingest(event)
    }
}

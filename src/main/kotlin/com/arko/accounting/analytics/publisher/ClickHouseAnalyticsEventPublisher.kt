package com.arko.accounting.analytics.publisher

import com.arko.accounting.analytics.events.AccountingEvent
import com.arko.analytics.service.AccountingAnalyticsIngestService
import org.springframework.stereotype.Component

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = ["app.clickhouse.enabled"], havingValue = "true", matchIfMissing = false)
class ClickHouseAnalyticsEventPublisher(
    private val ingestService: AccountingAnalyticsIngestService
) : AnalyticsEventPublisher {

    override fun publish(event: AccountingEvent) {
        ingestService.ingest(event)
    }
}

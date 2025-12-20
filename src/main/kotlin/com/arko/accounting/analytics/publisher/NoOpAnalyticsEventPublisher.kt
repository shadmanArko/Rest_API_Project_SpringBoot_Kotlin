package com.arko.accounting.analytics.publisher

import com.arko.accounting.analytics.events.AccountingEvent
import com.arko.accounting.analytics.events.CampaignEvent
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["app.clickhouse.enabled"],
    havingValue = "false",
    matchIfMissing = true
)
class NoOpAnalyticsEventPublisher : AnalyticsEventPublisher {

    override fun publish(event: AccountingEvent) {
        // no-op
    }

    override fun publishCampaignEvent(event: CampaignEvent) {
        // no-op
    }
}

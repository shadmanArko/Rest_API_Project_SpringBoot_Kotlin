package com.arko.accounting.analytics.publisher

import com.arko.accounting.analytics.events.AccountingEvent
import com.arko.accounting.analytics.events.CampaignEvent

interface AnalyticsEventPublisher {

    fun publish(event: AccountingEvent)

    fun publishCampaignEvent(event: CampaignEvent)
}

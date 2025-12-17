package com.arko.accounting.analytics.publisher

import com.arko.accounting.analytics.events.AccountingEvent

interface AnalyticsEventPublisher {
    fun publish(event: AccountingEvent)
}
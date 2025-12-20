package com.arko.accounting.analytics.events

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CampaignEvent(
    val eventDate: LocalDate,
    val companyId: UUID,
    val campaignId: UUID,
    val eventType: String, // COST / REVENUE
    val amount: BigDecimal
)

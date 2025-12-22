package com.arko.analytics.aggregation

import java.time.LocalDateTime

data class CampaignHourKey(
    val dateHour: LocalDateTime,
    val platform: String?,
    val accountId: String?,
    val campaignId: String?
)

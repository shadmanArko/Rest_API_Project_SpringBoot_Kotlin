package com.arko.analytics.dto

data class CampaignMetricsDto(
    val campaignId: String,
    val series: List<TimePointDto>
)

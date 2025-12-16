package com.arko.analytics.aggregation

data class AggregatedMetrics(
    var impressions: Long = 0,
    var clicks: Long = 0,
    var spend: Double = 0.0,
    var orders: Long = 0,
    var revenue: Double = 0.0
)

package com.arko.analytics.dto

import java.math.BigDecimal

data class OverviewDto(
    val totalSpend: BigDecimal,
    val totalRevenue: BigDecimal,
    val roas: BigDecimal,       // revenue / spend (0 if spend==0)
    val orders: Long,
    val avgCpa: BigDecimal      // spend / orders (0 if orders==0)
) {
    companion object {
        fun zero() = OverviewDto(
            totalSpend = BigDecimal.ZERO,
            totalRevenue = BigDecimal.ZERO,
            roas = BigDecimal.ZERO,
            orders = 0L,
            avgCpa = BigDecimal.ZERO
        )
    }

}


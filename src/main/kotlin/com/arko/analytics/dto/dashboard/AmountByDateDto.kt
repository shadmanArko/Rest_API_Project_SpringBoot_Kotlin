package com.arko.analytics.dto.dashboard

import java.math.BigDecimal


data class AmountByDateDto(
    val date: String,        // "2025-12-01"
    val amount: BigDecimal
)

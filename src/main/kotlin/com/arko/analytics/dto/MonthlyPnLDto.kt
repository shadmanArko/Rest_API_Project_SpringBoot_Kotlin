package com.arko.analytics.dto

import java.math.BigDecimal
import java.time.LocalDate

data class MonthlyPnLDto(
    val month: LocalDate,
    val income: BigDecimal,
    val expense: BigDecimal,
    val profit: BigDecimal
)

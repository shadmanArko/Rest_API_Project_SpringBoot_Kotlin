package com.arko.analytics.dto

import java.math.BigDecimal
import java.time.LocalDate

data class DailyFinancialDto(
    val date: LocalDate,
    val accountType: String, // EXPENSE / REVENUE
    val amount: BigDecimal
)

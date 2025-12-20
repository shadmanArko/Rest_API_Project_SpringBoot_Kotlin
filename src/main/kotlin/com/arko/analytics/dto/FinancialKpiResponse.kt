package com.arko.analytics.dto

import java.math.BigDecimal

data class FinancialKpiResponse(
    val period: String, // DAILY, MONTHLY, YEARLY
    val totalRevenue: BigDecimal,
    val totalExpense: BigDecimal,
    val grossProfit: BigDecimal,
    val netProfit: BigDecimal
)
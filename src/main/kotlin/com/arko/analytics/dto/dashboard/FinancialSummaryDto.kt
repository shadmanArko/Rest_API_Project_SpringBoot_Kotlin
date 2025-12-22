package com.arko.analytics.dto.dashboard

import java.math.BigDecimal

data class FinancialSummaryDto(
    val totalRevenue: BigDecimal,
    val totalExpense: BigDecimal,
    val netProfit: BigDecimal,
    val profitMarginPercent: BigDecimal
)
